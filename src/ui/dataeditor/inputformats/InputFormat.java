package ui.dataeditor.inputformats;

import dataconstructor.FieldData;
import ui.dataeditor.TableUpdater;

import javax.swing.*;

public abstract class InputFormat extends JPanel{

    protected TableUpdater updater;
    private String name;
    private int type;

    protected FieldData fieldData;

    public InputFormat(TableUpdater updater, String name, int type){
        this.updater = updater;
        this.name = name;
        this.type = type;
        initialize();
    }

    public abstract void initialize();

    //public abstract void setValue(Object object);

    public abstract Object defaultValue();

    public void setFieldData(FieldData fieldData){
        this.fieldData = fieldData;
        if(fieldData == null){
            return;
        }

        if(fieldData.getType() != type){
            fieldData.setType(type);
            fieldData.setValue(defaultValue());
        }
    }

    public int getType(){
        return type;
    }

    @Override
    public String toString(){
        return name;
    }
}
