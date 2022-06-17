package ui;

import dataconstructor.DataStructure;
import dataconstructor.DataUtils;
import ui.dataeditor.DataStructureTableModel;
import ui.dataeditor.FieldEditorPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

public class DataEditorPanel extends JPanel {

    //private static final String OUTPUT_DIR_LABEL = "Output Directory:";
    private static final String TITLE_FILE_MENU = "File";
    private static final String PACKAGE_LABEL = "Package:";
    private static final String NAME_LABEL = "Class Name:";
    private final File classTemplateDir;

    //private JLabel outputDirLabel;
    private JLabel nameLabel;
    private JLabel packageLabel;

    //private JTextField outputDirTextField;
    private JTextField classNameTextField;
    private JTextField packageNameTextField;

    private JPanel fileInfo;
    private FieldEditorPanel fieldEditor;

    private JPanel generatePanel;
    private JButton generateButton;

    private JMenuBar menuBar;

    private JMenu fileMenu;

    private JMenu newFile;
    private JMenuItem emptyFormatItem;
    private JMenuItem templateFormatItem;

    private JMenuItem open;
    private JMenuItem save;
    private JMenuItem saveAs;

    private JMenuItem saveAsTemplate;

    private JMenuItem exit;

    private JFileChooser fileChooser;
    private JFileChooser templateChooser;
    private FileView fileView;
    private File file;

    public DataEditorPanel(){
        classTemplateDir = new File(System.getProperty("user.dir") + "/ClassTemplates/");
        if(!classTemplateDir.exists()){
            classTemplateDir.mkdirs();
        }

        setLayout(new BorderLayout());

        menuBar = new JMenuBar();
        fileMenu = new JMenu(TITLE_FILE_MENU);

        fileView = new FileView(){
            @Override
            public Boolean isTraversable(File file){

                Path templatePath = null;
                Path path = null;

                try {
                    templatePath = classTemplateDir.toPath();
                    path = file.toPath();
                }catch(InvalidPathException e){
                    return false;
                }

                if(path.getNameCount() < templatePath.getNameCount() || file.isFile()){
                    return false;
                }

                int pathCount = path.getNameCount();
                int templatePathCount = templatePath.getNameCount();
                int calculatedPathCount = pathCount - (pathCount - templatePathCount);

                return path.getName(calculatedPathCount - 1).toString().equals(templatePath.getName(templatePathCount - 1).toString());

                //return CLASS_TEMPLATES.equals(file);
            }
        };

        fileChooser = new JFileChooser(){
            @Override
            public void approveSelection(){
                File file = getSelectedFile();
                if(file.exists() && getDialogType() == SAVE_DIALOG){
                    int result = JOptionPane.showConfirmDialog(this, "Overwrite this file?", "Existing file", JOptionPane.YES_NO_OPTION);

                    switch(result){
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.NO_OPTION:
                            return;
                        case JOptionPane.CLOSED_OPTION:
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            cancelSelection();
                            return;
                    }
                }

                super.approveSelection();
            }
        };
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        templateChooser = new JFileChooser(){
            @Override
            public void approveSelection(){
                File file = getSelectedFile();
                if(file.exists() && getDialogType() == SAVE_DIALOG){
                    int result = JOptionPane.showConfirmDialog(this, "Overwrite this file?", "Existing file", JOptionPane.YES_NO_OPTION);

                    switch(result){
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.NO_OPTION:
                            return;
                        case JOptionPane.CLOSED_OPTION:
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            cancelSelection();
                            return;
                    }
                }

                super.approveSelection();
            }
        };

        templateChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        templateChooser.setCurrentDirectory(classTemplateDir);
        templateChooser.setFileView(fileView);

        menuBar.add(fileMenu);

        newFile = new JMenu("New Data File");

        emptyFormatItem = new JMenuItem(new AbstractAction("Empty Format") {

            private final Object[] options = {"Yes", "No"};

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!fieldEditor.isSaved()) {
                    int result = JOptionPane.showOptionDialog(DataEditorPanel.this, "Save current data structure?", "Message", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if(result == JOptionPane.YES_OPTION){
                        if(!dataValidCheck()){
                            return;
                        }

                        if(file == null){
                            openFileChooser(true);
                            file = fileChooser.getSelectedFile();
                        }
                        saveDataStructure();

                    }else if(result == JOptionPane.CANCEL_OPTION){
                        return;
                    }
                }

                classNameTextField.setText("");
                packageNameTextField.setText("");

                fieldEditor.setTableModel(new DataStructureTableModel());
                file = null;
            }
        });

        templateFormatItem = new JMenuItem(new AbstractAction("Template Format") {

            private final Object[] options = {"Yes", "No"};

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!fieldEditor.isSaved()) {
                    int result = JOptionPane.showOptionDialog(DataEditorPanel.this, "Save current data structure?", "Message", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if(result == JOptionPane.YES_OPTION){
                        if(!dataValidCheck()){
                            return;
                        }

                        if(file == null){
                            openFileChooser(true);
                            file = fileChooser.getSelectedFile();
                        }
                        saveDataStructure();

                    }else if(result == JOptionPane.CANCEL_OPTION){
                        return;
                    }
                }

                if(openTemplateChooser(false) == JFileChooser.APPROVE_OPTION){
                    file = templateChooser.getSelectedFile();
                    /*Path dirPath = classTemplateDir.toPath();
                    Path filePath = file.toPath();
                    Path subPath = filePath.subpath(dirPath.getNameCount() - 1, filePath.getNameCount());

                    System.out.println("subpath = " + subPath.toString());*/
                    openDataStructure();
                }
            }
        });


        newFile.add(emptyFormatItem);
        newFile.add(templateFormatItem);

        open = new JMenuItem(new AbstractAction("Open") {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(openFileChooser(false) == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                    openDataStructure();
                }
            }
        });

        save = new JMenuItem(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!dataValidCheck()){
                    return;
                }

                if(file == null){
                    if(openFileChooser(true) == JFileChooser.APPROVE_OPTION){
                        file = fileChooser.getSelectedFile();
                    }else{
                        return;
                    }
                }

                saveDataStructure();
            }
        });

        saveAs = new JMenuItem(new AbstractAction("Save As") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!dataValidCheck()) {
                    return;
                }

                if(openFileChooser(true) == JFileChooser.APPROVE_OPTION){
                    file = fileChooser.getSelectedFile();
                    saveDataStructure();
                }

            }
        });

        saveAsTemplate = new JMenuItem(new AbstractAction("Save As Template") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Path = " + new File(System.getProperty("user.dir")+"/ClassTemplates/").getPath());
                if(!dataValidCheck()){
                    return;
                }

                if(openTemplateChooser(true) == JFileChooser.APPROVE_OPTION){
                    file = templateChooser.getSelectedFile();
                    saveDataStructure();
                };

            }
        });

        exit = new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(newFile);
        fileMenu.add(open);
        fileMenu.addSeparator();
        fileMenu.add(save);
        fileMenu.add(saveAs);
        fileMenu.add(saveAsTemplate);
        fileMenu.addSeparator();
        fileMenu.add(exit);

        fileInfo = new JPanel();
        fileInfo.setLayout(new GridBagLayout());
        fileInfo.setBorder(new EmptyBorder(10, 10, 10, 10));

        nameLabel = new JLabel(NAME_LABEL);
        packageLabel = new JLabel(PACKAGE_LABEL);

        classNameTextField = new JTextField();
        packageNameTextField = new JTextField();

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx = 0;
        c.insets = new Insets(0 ,0 ,0, 10);

        fileInfo.add(packageLabel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 1;
        c.insets = new Insets(0 ,0 ,0, 0);

        fileInfo.add(packageNameTextField, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5 ,0 ,0, 10);

        fileInfo.add(nameLabel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(5 ,0 ,0, 0);

        fileInfo.add(classNameTextField, c);

        fieldEditor = new FieldEditorPanel(null);


        /*generatePanel = new JPanel();
        generatePanel.setLayout(new GridBagLayout());
        generatePanel.setBorder(new EmptyBorder(10, 10, 10 ,10));

        generateButton = new JButton("Generate File");
        generateButton.setPreferredSize(new Dimension(200, 30));

        generatePanel.add(generateButton);*/

        add(fileInfo, BorderLayout.PAGE_START);
        add(fieldEditor, BorderLayout.CENTER);
        //add(generatePanel, BorderLayout.PAGE_END);
    }

    public void setClassName(String name){
        classNameTextField.setText(name);
    }

    public String getName(){
        return classNameTextField.getText();
    }

    /*public FieldEditorPanel fieldEditor(){
        return fieldEditor;
    }*/

    public JMenuBar menuBar(){
        return menuBar;
    }

    private boolean dataValidCheck(){
        List<String> invalids;
        invalids = fieldEditor.checkDataValidation();

        if(invalids.size() > 0){
            StringBuilder text = new StringBuilder();
            for(int i = 0; i < invalids.size(); i++){
                String invalid = invalids.get(i);
                text.append(invalid).append("\n");
            }

            JOptionPane.showMessageDialog(DataEditorPanel.this, text.toString(), "Warning", JOptionPane.WARNING_MESSAGE);
        }

        return invalids.size() == 0;

    }

    private int openFileChooser(boolean save){

        int result = JFileChooser.CANCEL_OPTION;
        if(save) {
            fileChooser.setSelectedFile(new File(getName() + ".data"));
            result = fileChooser.showSaveDialog(DataEditorPanel.this);
        }else{
            result = fileChooser.showDialog(DataEditorPanel.this, "Open");
        }
        /*if(result == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }*/

        return result;
    }

    private int openTemplateChooser(boolean save){

        int result = JFileChooser.CANCEL_OPTION;
        if(save) {
            templateChooser.setSelectedFile(new File(getName() + ".template"));
            result = templateChooser.showSaveDialog(DataEditorPanel.this);
        }else{
            result = templateChooser.showDialog(DataEditorPanel.this, "Open");
        }
        /*if(result == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }*/

        return result;
    }

    private void saveDataStructure(){
        if(file == null){
            return;
        }

        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file, false);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(DataUtils.convertToDataStructure(packageNameTextField.getText() + "." + classNameTextField.getText(), fieldEditor.getTable()));
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(DataEditorPanel.this, "Unable to save file!", "Error", JOptionPane.ERROR_MESSAGE);
            exception.printStackTrace();
        }
    }

    private void openDataStructure(){
        if(file == null) {
            return;
        }

        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        DataStructureTableModel tableModel = null;
        try {

            fileInputStream = new FileInputStream(file);
            objectInputStream = new ObjectInputStream(fileInputStream);

            DataStructure dataStructure = (DataStructure) objectInputStream.readObject();

            int index = dataStructure.className.lastIndexOf('.');

            //System.out.println(dataStructure.className);
            if(index > 0){
                classNameTextField.setText(dataStructure.className.substring(index + 1));
                packageNameTextField.setText(dataStructure.className.substring(0, index));
            }else{
                classNameTextField.setText(dataStructure.className);
                packageNameTextField.setText("");
            }

            tableModel = DataUtils.convertToDataStructureTableModel(dataStructure);

            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException | ClassNotFoundException exception) {
            //exception.printStackTrace();
            JOptionPane.showMessageDialog(DataEditorPanel.this, "Invalid file type or version!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (tableModel != null) {
            //setClassName(file.getName());
            fieldEditor.setTableModel(tableModel);
        }

    }

    private void setClassSignature(DataStructure dataStructure){

    }

}
