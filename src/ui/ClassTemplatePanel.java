package ui;

import dataconstructor.DataStructure;
import dataconstructor.DataUtils;
import dataconstructor.FieldData;
import ui.classtemplate.TypeInfo;
import ui.dataeditor.DataStructureTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClassTemplatePanel extends JPanel {

    private static final String[] COLUMN_NAMES = new String[]{"Index", "Name", "Type"};
    private static final String TITLE_FILE_MENU = "File";

    private JMenuBar menuBar;

    private JMenu fileMenu;

    private JMenuItem newFile;
    private JMenuItem open;
    private JMenuItem save;
    private JMenuItem saveAs;
    private JMenuItem exit;

    private JScrollPane scrollPane;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox comboBox;

    private JButton addFieldButton;
    private JButton removeFieldButton;

    private JFileChooser fileChooser;
    private File file;

    private boolean saved;

    //TODO Menu and input/output

    public ClassTemplatePanel(DefaultTableModel tableModel){

        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();

        menuBar = new JMenuBar();
        fileMenu = new JMenu(TITLE_FILE_MENU);

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //TODO lock to only resource directory

        menuBar.add(fileMenu);

        newFile = new JMenuItem(new AbstractAction("New File") {

            private final Object[] options = {"Yes", "No"};

            @Override
            public void actionPerformed(ActionEvent e) {
                if(saved) {
                    int result = JOptionPane.showOptionDialog(ClassTemplatePanel.this, "Save current data structure?", "Message", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if(result == JOptionPane.YES_OPTION){
                        if(!dataValidCheck()){
                            return;
                        }

                        if(file == null){
                            openSaveDialog();
                        }
                        save();

                    }else if(result == JOptionPane.CANCEL_OPTION){
                        return;
                    }
                }

                file = null;
            }
        }){
            @Override
            public Dimension getPreferredSize(){
                Dimension d = super.getPreferredSize();
                d.width = 120;
                return d;
            }
        };

        open = new JMenuItem(new AbstractAction("Open") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showDialog(ClassTemplatePanel.this, "Open");

                if(result == JFileChooser.APPROVE_OPTION) {

                    file = fileChooser.getSelectedFile();

                    FileInputStream fileInputStream = null;
                    ObjectInputStream objectInputStream = null;
                    DataStructureTableModel tableModel = null;
                    try {

                        fileInputStream = new FileInputStream(file);
                        objectInputStream = new ObjectInputStream(fileInputStream);

                        setName(file.getName());
                        DataStructure dataStructure = (DataStructure) objectInputStream.readObject();
                        tableModel = DataUtils.convertToDataStructureTableModel(dataStructure);

                        objectInputStream.close();
                        fileInputStream.close();
                    } catch (IOException | ClassNotFoundException exception) {
                        JOptionPane.showMessageDialog(ClassTemplatePanel.this, "Invalid file type!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    if (tableModel != null) {
                        //setTableModel(tableModel);
                    }
                }
            }
        });

        save = new JMenuItem(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(dataValidCheck()){
                    return;
                }

                if(file == null){
                    openSaveDialog();
                }

                save();
            }
        });

        saveAs = new JMenuItem(new AbstractAction("Save As") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dataValidCheck()) {
                    return;
                }

                openSaveDialog();
                save();
            }
        });

        exit = new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO create exit event

            }
        });

        fileMenu.add(newFile);
        fileMenu.add(open);
        fileMenu.addSeparator();
        fileMenu.add(save);
        fileMenu.add(saveAs);
        fileMenu.addSeparator();
        fileMenu.add(exit);

        comboBox = new JComboBox();
        comboBox.addItem(new TypeInfo(FieldData.NO_TYPE, -1));
        comboBox.addItem(new TypeInfo("Integer", DataStructure.TYPE_INT));
        comboBox.addItem(new TypeInfo("Float", DataStructure.TYPE_FLOAT));
        comboBox.addItem(new TypeInfo("Boolean", DataStructure.TYPE_BOOLEAN));
        comboBox.addItem(new TypeInfo("Character", DataStructure.TYPE_CHAR));
        comboBox.addItem(new TypeInfo("String", DataStructure.TYPE_STRING));
        comboBox.addItem(new TypeInfo("Object", DataStructure.TYPE_OBJECT));

        this.tableModel = tableModel;

        if(tableModel == null){
            tableModel = setTableModel();
            this.tableModel = tableModel;
            //System.out.println(this.tableModel.getColumnCount());
        }

        table = new JTable(this.tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        /*table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRowIndex = table.getSelectedRow();
                String value = (String)table.getValueAt(selectedRowIndex, 1);

                if(value.equals("")){
                    ClassTemplatePanel.this.tableModel.setValueAt(FieldData.NO_NAME, selectedRowIndex, 1);
                    ClassTemplatePanel.this.tableModel.fireTableCellUpdated(selectedRowIndex, 1);
                }

            }
        });*/
        JTextField textField = new JTextField();
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if(textField.getText().equals(FieldData.NO_NAME)) {
                    textField.select(0, textField.getText().length());
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.select(0, 0);
            }
        });

        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(textField){
            @Override
            public boolean stopCellEditing(){
                boolean b = super.stopCellEditing();
                int selectedRowIndex = table.getSelectedRow();
                String value = ((JTextField)editorComponent).getText();
                if(value.equals("")){
                    ClassTemplatePanel.this.tableModel.setValueAt(FieldData.NO_NAME, selectedRowIndex, 1);
                    ClassTemplatePanel.this.tableModel.fireTableCellUpdated(selectedRowIndex, 1);
                }
                return b;
            }
        });
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBox));

        scrollPane = new JScrollPane(table);

        removeFieldButton = new JButton("-");
        removeFieldButton.setFont(new Font(removeFieldButton.getFont().getName(), Font.PLAIN, 20));
        addFieldButton = new JButton("+");
        addFieldButton.setFont(new Font(removeFieldButton.getFont().getName(), Font.PLAIN, 20));

        removeFieldButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                removeField();
            }

        });

        addFieldButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                addField();
            }

        });

        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 10 ,0);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.weighty = 0;
        add(removeFieldButton, c);

        c.gridx = 1;

        add(addFieldButton, c);

    }

    public JMenuBar menu(){
        return menuBar;
    }

    public FieldData[] obtainData(){
        return null;
    }

    private void addField(){
        //FieldData fieldData = new FieldData();
        tableModel.addRow(new Object[]{tableModel.getRowCount(), FieldData.NO_NAME, FieldData.NO_TYPE});
    }

    private void removeField(){
        int index = table.getSelectedRow();

        if (index != -1) {
            tableModel.removeRow(index);
            for(int i = 0; i < tableModel.getRowCount(); i++){
                tableModel.setValueAt(i, i, 0);
            }
        }
    }

    private DefaultTableModel setTableModel(){

        DefaultTableModel model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column){
                return column != 0;
            }
        };
        model.setRowCount(0);
        model.setColumnCount(COLUMN_NAMES.length);
        model.setColumnIdentifiers(COLUMN_NAMES);

        return model;
    }

    private boolean dataValidCheck(){
        List<String> invalids = new ArrayList<>();
        //invalids = fieldEditor.checkDataValidation();

        for(int i = 0; i < tableModel.getRowCount(); i++){
            String name = (String)tableModel.getValueAt(i, 1);
            int type = (int)tableModel.getValueAt(i, 2);

            if(name.equals(FieldData.NO_NAME) || name.equals("")){
                invalids.add("Field [" + i + "] has no name!");
            }

            if(type == -1){
                invalids.add("Field [" + i + "] has empty type!");
            }
        }

        if(invalids.size() > 0){
            StringBuilder text = new StringBuilder();
            for(int i = 0; i < invalids.size(); i++){
                String invalid = invalids.get(i);
                text.append(invalid).append("\n");
            }

            JOptionPane.showMessageDialog(this, text.toString(), "Warning", JOptionPane.WARNING_MESSAGE);
        }

        return invalids.size() == 0;
    }

    private void openSaveDialog(){
        fileChooser.setSelectedFile(new File(getName()));
        int result = fileChooser.showSaveDialog(this);
        if(result == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }
    }

    private void save(){
        if(file == null){
            return;
        }

        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file, false);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            //objectOutputStream.writeObject(DataUtils.convertToDataStructure(fieldEditor.getTable()));
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unable to save file!", "Error", JOptionPane.ERROR_MESSAGE);
            exception.printStackTrace();
        }
    }
}
