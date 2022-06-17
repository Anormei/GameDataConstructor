package ui.dataeditor.inputformats;

import dataconstructor.DataStructure;
import dataconstructor.FieldData;
import ui.dataeditor.TableUpdater;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class CharacterInputFormat extends InputFormat{

    private JTextField value;

    public CharacterInputFormat(TableUpdater updater) {
        super(updater, "Character", DataStructure.TYPE_CHAR);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        value = new JTextField();
        value.setDocument(new JTextFieldLimit(1));

        value.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }

            private void update(){
                String text = value.getText();

                if(text.length() > 0) {
                    fieldData.setValue(value.getText().charAt(0));
                }else{
                    fieldData.setValue(null);
                }
                updater.updateTable(updater.getSelectedRowIndex(), fieldData);
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

        value.setText(String.valueOf(fieldData.getValue()));
        updater.updateTable(updater.getSelectedRowIndex(), fieldData);
    }

    @Override
    public Object defaultValue() {
        return 'a';
    }
}
