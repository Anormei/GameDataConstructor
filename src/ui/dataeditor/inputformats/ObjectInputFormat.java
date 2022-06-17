package ui.dataeditor.inputformats;

import dataconstructor.DataStructure;
import dataconstructor.FieldData;
import ui.dataeditor.DataStructureTableModel;
import ui.dataeditor.TableUpdater;
import ui.dataeditor.DataButton;

import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ObjectInputFormat extends InputFormat {

    //private JFrame parentFrame;
    private DataButton dataButton;

    public ObjectInputFormat(TableUpdater updater){
        super(updater, "Object", DataStructure.TYPE_OBJECT);
        //parentFrame = frame;
        dataButton = new DataButton(null);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        add(dataButton, c);

    }

    @Override
    public void initialize() {

    }

    @Override
    public void setFieldData(FieldData fieldData) {
        super.setFieldData(fieldData);

        /*GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        add(dataButton, c);*/
        dataButton.setObjectData((DataStructureTableModel) fieldData.getValue());
        updater.updateTable(updater.getSelectedRowIndex(), fieldData);
    }


    @Override
    public Object defaultValue() {

        DefaultTableModel fieldListModel = new DataStructureTableModel();

        return fieldListModel;
    }
}
