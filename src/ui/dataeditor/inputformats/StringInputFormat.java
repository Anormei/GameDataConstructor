package ui.dataeditor.inputformats;

import dataconstructor.DataStructure;
import dataconstructor.FieldData;
import ui.dataeditor.TableUpdater;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class StringInputFormat extends InputFormat{

    private JTextField value;

    public StringInputFormat(TableUpdater updater){
        super(updater, "String", DataStructure.TYPE_STRING);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        value = new JTextField();

        value.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                //System.out.println("Updating");
                fieldData.setValue(value.getText());

                updater.updateTable(updater.getSelectedRowIndex(), fieldData);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fieldData.setValue(value.getText());
                updater.updateTable(updater.getSelectedRowIndex(), fieldData);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        add(value, c);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void setFieldData(FieldData fieldData) {
        super.setFieldData(fieldData);

        value.setText((String)fieldData.getValue());
        updater.updateTable(updater.getSelectedRowIndex(), fieldData);
    }

    @Override
    public Object defaultValue() {
        return "";
    }
}
