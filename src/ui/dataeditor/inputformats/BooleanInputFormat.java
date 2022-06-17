package ui.dataeditor.inputformats;

import dataconstructor.DataStructure;
import dataconstructor.FieldData;
import ui.dataeditor.TableUpdater;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BooleanInputFormat extends InputFormat implements ActionListener {

     private JRadioButton trueRadioButton;
     private JRadioButton falseRadioButton;

     private ButtonGroup group;

    public BooleanInputFormat(TableUpdater updater){
        super(updater, "Boolean", DataStructure.TYPE_BOOLEAN);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        trueRadioButton = new JRadioButton("true");
        falseRadioButton = new JRadioButton("false");

        group = new ButtonGroup();
        group.add(trueRadioButton);
        group.add(falseRadioButton);

        group.setSelected(falseRadioButton.getModel(), true);

        trueRadioButton.addActionListener(this);
        falseRadioButton.addActionListener(this);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.weighty = 1.0;

        add(trueRadioButton, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.weighty = 1.0;

        add(falseRadioButton, c);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fieldData.setValue(group.getSelection() == trueRadioButton.getModel());
        updater.updateTable(updater.getSelectedRowIndex(), fieldData);
    }

    /*@Override
    public void setValue(Object object) {
        boolean bool = (boolean) object;
        group.setSelected(trueRadioButton.getModel(), bool);
        group.setSelected(falseRadioButton.getModel(), !bool);
    }*/

    @Override
    public void setFieldData(FieldData fieldData) {
        super.setFieldData(fieldData);

        boolean bool = (boolean) fieldData.getValue();
        group.setSelected(trueRadioButton.getModel(), bool);
        group.setSelected(falseRadioButton.getModel(), !bool);
        updater.updateTable(updater.getSelectedRowIndex(), fieldData);
    }

    @Override
    public Object defaultValue() {
        return false;
    }
}
