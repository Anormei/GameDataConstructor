package ui;

import axismaker.AxisFactory;
import axismaker.Vector2;
import dataconstructor.DataStructure;
import dataconstructor.DataUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static axismaker.AxisFactory.traceImage;

public class CollisionPanel extends JPanel {

    private JLabel title;
    private JLabel inputLabel;
    private JLabel outputLabel;

    private JTextField inputText;
    private JTextField outputText;

    private JButton openInput;
    private JButton openOutput;
    private JButton generateButton;

    private JFileChooser fileChooser;

    private File inputDir;
    private File outputDir;

    //TODO Collision panel
    public CollisionPanel(){
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        inputDir = null;
        outputDir = null;

        title = new JLabel("Axis Maker");
        title.setFont(new Font(title.getFont().getName(), Font.PLAIN, 32));
        inputLabel = new JLabel("Input Directory");
        outputLabel = new JLabel("Output Directory");

        inputText = new JTextField();
        outputText = new JTextField();

        inputText.setPreferredSize(new Dimension(300, 21));
        outputText.setPreferredSize(new Dimension(300,21));

        //inputText.setColumns(200);
        //outputText.setColumns(200);

        inputText.setEditable(false);
        outputText.setEditable(false);

        openInput = new JButton("Change");
        openInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(CollisionPanel.this);
                if(result == JFileChooser.APPROVE_OPTION){
                    inputDir = fileChooser.getSelectedFile();
                    inputText.setText(inputDir.getPath());
                }
            }
        });

        openOutput = new JButton("Change");
        openOutput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(CollisionPanel.this);
                if(result == JFileChooser.APPROVE_OPTION){
                    outputDir = fileChooser.getSelectedFile();
                    outputText.setText(outputDir.getPath());
                }
            }
        });

        generateButton = new JButton("Generate Files");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(inputDir == null || outputDir == null){
                    JOptionPane.showMessageDialog(CollisionPanel.this, "Directory paths must not be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                /*Path path = inputDir.toPath();
                int length = path.getNameCount();

                Path subPath = path.subpath(length - 1, length);

                System.out.println("path = " + subPath.toString());*/
                generateFiles(inputDir);

            }
        });

        c.insets = new Insets(0, 0, 50, 0);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;

        add(title, c);

        c.insets = new Insets(0, 0, 20, 0);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;

        add(inputLabel, c);

        c.insets = new Insets(0, 5, 20, 5);

        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;

        add(inputText, c);

        c.insets = new Insets(0, 0, 20, 0);

        c.gridx = 2;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;

        add(openInput, c);

        c.insets = new Insets(0, 0, 0, 0);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;

        add(outputLabel, c);

        c.insets = new Insets(0, 5, 0, 5);

        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;

        add(outputText, c);

        c.insets = new Insets(0, 0, 0, 0);

        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;

        add(openOutput, c);

        c.insets = new Insets(30, 0, 0, 0);

        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;

        add(generateButton, c);
    }

    private void generateFiles(File dir){
        if(!dir.isDirectory()){
            return;
        }

        File[] files = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".png");
            }
        });

        if(files == null || files.length == 0){
            return;
        }

        float width = 0;
        float height = 0;
        Vector2 pos = new Vector2();
        float bodyWidth = 0;
        float bodyHeight = 0;

        for(int i = 0; i < files.length; i++){
            File file = files[i];
            if(file.isDirectory()){
                generateFiles(file);
                continue;
            }

            bodyWidth = 0;
            bodyHeight = 0;

            List<Vector2> trace = AxisFactory.traceImage(file.getPath());
            List<List<Vector2>> axes = AxisFactory.shatterAxis(trace);

            FileOutputStream fileOutputStream = null;
            ObjectOutputStream objectOutputStream = null;

            DataStructure body = new DataStructure(4);
            body.setData(0, "hitBoxes", DataStructure.TYPE_OBJECT, new DataStructure(axes.size()));
            for(int j = 0; j < axes.size(); j++){
                List<Vector2> axis = axes.get(j);
                DataStructure hitBoxes = (DataStructure)body.values[0];


                DataStructure hitBox = new DataStructure(4);
                DataStructure vertices = new DataStructure(axis.size());

                width = 0;
                height = 0;
                pos.set(Float.MAX_VALUE, Float.MAX_VALUE);

                for(int k = 0; k < axis.size(); k++){
                    Vector2 vector = axis.get(k);
                    //DataStructure hitBox = (DataStructure)hitBoxes.values[j];
                    DataStructure vectorData = new DataStructure(2);

                    vectorData.setData(0, "x", DataStructure.TYPE_FLOAT, vector.x);
                    vectorData.setData(1, "y", DataStructure.TYPE_FLOAT, vector.y);

                    vertices.setData(k, "", DataStructure.TYPE_OBJECT, vectorData);

                    if(vector.x < pos.x) {
                        pos.x = vector.x;
                    }
                    if(vector.y < pos.y) {
                        pos.y = vector.y;
                    }
                    if(vector.x > width) {
                        width = vector.x;
                    }
                    if(vector.y > height) {
                        height = vector.y;
                    }
                    if(vector.x > bodyWidth) {
                        bodyWidth = vector.x;
                    }
                    if(vector.y > bodyHeight) {
                        bodyHeight = vector.y;
                    }

                }

                width -= pos.x;
                height -= pos.y;

                hitBox.setData(0, "vertices", DataStructure.TYPE_OBJECT, vertices);
                hitBox.setData(1, "pos", DataStructure.TYPE_OBJECT, new DataStructure(
                        new String[]{"x", "y"},
                        new int[]{DataStructure.TYPE_FLOAT, DataStructure.TYPE_FLOAT},
                        new Object[]{pos.x, pos.y}
                        )
                );

                hitBox.setData(2, "width", DataStructure.TYPE_FLOAT, width);
                hitBox.setData(3, "height", DataStructure.TYPE_FLOAT, height);


                hitBoxes.names[j] = "";
                hitBoxes.types[j] = DataStructure.TYPE_OBJECT;
                hitBoxes.values[j] = hitBox;

            }

            //TODO subpath
            Path dirPath = outputDir.toPath();
            Path filePath = file.toPath();
            String fileName = filePath.subpath(dirPath.getNameCount(), filePath.getNameCount()).toString();
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));

            body.setData(1, "fileName", DataStructure.TYPE_STRING, fileName);
            body.setData(2, "width", DataStructure.TYPE_FLOAT, bodyWidth);
            body.setData(3, "height", DataStructure.TYPE_FLOAT, bodyHeight);

            fileName += ".body";

            File outputFile = new File(outputDir.getPath() + File.separator + fileName);
            outputFile.getParentFile().mkdirs();

            try {
                fileOutputStream = new FileOutputStream(outputFile);
                objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(body);
                objectOutputStream.close();
                fileOutputStream.close();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Unable to convert files!", "Error", JOptionPane.ERROR_MESSAGE);
                exception.printStackTrace();
                return;
            }
        }
    }
}
