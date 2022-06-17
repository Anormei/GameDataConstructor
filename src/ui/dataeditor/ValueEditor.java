package ui.dataeditor;

import dataconstructor.FieldData;
import ui.dataeditor.inputformats.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;

public class ValueEditor extends JPanel {

    private TableUpdater tableUpdater;
    private FieldData fieldData;
    private int index;

    private JPanel subPanel;

    private JLabel fieldNameLabel;
    private JTextField fieldNameText;

    private JLabel fieldTypeLabel;
    private JComboBox<InputFormat> fieldTypeComboBox;

    private JLabel fieldValueLabel;
    private JTextField fieldValueTextEditor;

    private InputFormat currentInputFormat;

    private Map<Integer, InputFormat> inputFormats = new HashMap<>();

    public ValueEditor(TableUpdater tableUpdater){

        this.tableUpdater = tableUpdater;
        subPanel = new JPanel();
        subPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        setLayout(new GridLayout(1 ,0));

        fieldNameLabel = new JLabel("Field Name:");
        fieldNameText = new JTextField();
        fieldNameText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fieldData.setName(fieldNameText.getText());
                tableUpdater.updateTable(index, fieldData);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fieldData.setName(fieldNameText.getText());
                tableUpdater.updateTable(index, fieldData);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        fieldNameText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if(fieldNameText.getText().equals(FieldData.NO_NAME)) {
                    fieldNameText.select(0, fieldNameText.getText().length());
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                fieldNameText.select(0, 0);
            }
        });

        fieldTypeLabel = new JLabel("Field Type:");
        fieldTypeComboBox = new JComboBox<>();

        fieldValueLabel = new JLabel("Value:");
        fieldValueTextEditor = new JTextField();

        fieldTypeComboBox.addItem(new IntegerInputFormat(tableUpdater));
        fieldTypeComboBox.addItem(new FloatInputFormat(tableUpdater));
        fieldTypeComboBox.addItem(new BooleanInputFormat(tableUpdater));
        fieldTypeComboBox.addItem(new CharacterInputFormat(tableUpdater));
        fieldTypeComboBox.addItem(new StringInputFormat(tableUpdater));
        fieldTypeComboBox.addItem(new ObjectInputFormat(tableUpdater));

        ComboBoxModel<InputFormat> comboBoxModel = fieldTypeComboBox.getModel();
        for(int i = 0; i < comboBoxModel.getSize(); i++){
            InputFormat inputFormat = comboBoxModel.getElementAt(i);

            inputFormats.put(inputFormat.getType(), inputFormat);
        }

        fieldTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentInputFormat != null) {
                    subPanel.remove(currentInputFormat);
                }

                placeInput((InputFormat)fieldTypeComboBox.getSelectedItem());

                if(currentInputFormat != null){
                    currentInputFormat.setFieldData(fieldData);

                    /*if(fieldData.getType() != currentInputFormat.getType()){
                        fieldData.setType(currentInputFormat.getType());
                    }else if(fieldData.getValue() != null){
                        currentInputFormat.setValue(fieldData.getValue());
                    }else{
                        defaultValueInput();
                    }*/
                }

                revalidate();
                repaint();
            }
        });

        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 1;

        subPanel.add(fieldNameLabel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 1;

        subPanel.add(fieldNameText, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.weightx = 1;

        subPanel.add(fieldTypeLabel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.weightx = 1;

        subPanel.add(fieldTypeComboBox, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        c.weightx = 1;

        subPanel.add(fieldValueLabel, c);

        add(subPanel);
    }

    public void setData(int index, FieldData fieldData){

        this.fieldData = fieldData;
        this.index = index;

        if(fieldData == null){
            return;
        }

        fieldNameText.setText(fieldData.toString());
        if(currentInputFormat != null) {
            subPanel.remove(currentInputFormat);
        }

        placeInput(inputFormats.get(fieldData.getType()));
        fieldTypeComboBox.setSelectedItem(currentInputFormat);

    }

    private void placeInput(InputFormat inputFormat){

        currentInputFormat = inputFormat;
        if(currentInputFormat == null){
            return;
        }

        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 1;
        c.weightx = 1;

        subPanel.add(currentInputFormat, c);
        revalidate();
        repaint();
    }

    @Override
    public void setVisible(boolean val){
        subPanel.setVisible(val);
    }

}
