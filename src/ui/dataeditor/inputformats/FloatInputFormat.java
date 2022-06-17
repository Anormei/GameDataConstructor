package ui.dataeditor.inputformats;

import dataconstructor.DataStructure;
import dataconstructor.FieldData;
import ui.dataeditor.TableUpdater;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.InternationalFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class FloatInputFormat extends InputFormat{

    JFormattedTextField value;

    public FloatInputFormat(TableUpdater updater) {
        super(updater,"Float", DataStructure.TYPE_FLOAT);
        setLayout(new GridBagLayout());
        //setBorder(new EmptyBorder(10, 0, 0 ,0));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        NumberFormat format = DecimalFormat.getInstance();
        format.setGroupingUsed(false);

        InternationalFormatter formatter = new InternationalFormatter((format));
        formatter.setValueClass(Float.class);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(true);

        value = new JFormattedTextField(formatter);
        value.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    value.commitEdit();
                }catch(ParseException exception){
                    //exception.printStackTrace();
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
        return 0.0f;
    }
}
