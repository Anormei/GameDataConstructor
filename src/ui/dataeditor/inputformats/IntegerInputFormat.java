package ui.dataeditor.inputformats;

import dataconstructor.DataStructure;
import dataconstructor.FieldData;
import ui.dataeditor.TableUpdater;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;
import java.text.ParseException;

public class IntegerInputFormat extends InputFormat {

    JFormattedTextField value;

    public IntegerInputFormat(TableUpdater updater) {
        super(updater,"Integer", DataStructure.TYPE_INT);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);

        NumberFormatter formatter = new NumberFormatter((format));
        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(true);

        value = new JFormattedTextField(formatter);

        value.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

                try {
                    value.commitEdit();
                }catch(ParseException exception){
                    exception.printStackTrace();
                }
                fieldData.setValue(value.getValue());
                updater.updateTable(updater.getSelectedRowIndex(), fieldData);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

                try {
                    value.commitEdit();
                }catch(ParseException exception){
                    //exception.printStackTrace();
                }
                fieldData.setValue(value.getValue());
                updater.updateTable(updater.getSelectedRowIndex(), fieldData);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }

            public void log(){
                System.out.println("value = " + value.getValue());
                System.out.println("textfield = " + value.getText());
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

        value.setValue(fieldData.getValue());
        updater.updateTable(updater.getSelectedRowIndex(), fieldData);
    }

    @Override
    public Object defaultValue() {
        return 0;
    }
}
