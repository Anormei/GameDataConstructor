package dataconstructor;

import ui.dataeditor.DataStructureTableModel;

public class DataUtils {

    public static DataStructure convertToDataStructure(DataStructureTableModel table){
        int size = table.getRowCount();
        DataStructure dataStructure = new DataStructure(size);
        for(int i = 0; i < size; i++){
            FieldData fieldData = (FieldData)table.getValueAt(i, 2);
            dataStructure.types[i] = fieldData.getType();
            dataStructure.names[i] = fieldData.getName();
            if(fieldData.getType() != DataStructure.TYPE_OBJECT) {
                dataStructure.values[i] = fieldData.getValue();
            }else{
                dataStructure.values[i] = convertToDataStructure((DataStructureTableModel) fieldData.getValue());
            }
        }

        return dataStructure;
    }

    public static DataStructure convertToDataStructure(String className, DataStructureTableModel table){
        DataStructure dataStructure = convertToDataStructure(table);
        dataStructure.className = className;
        return dataStructure;
    }

    public static DataStructure convertToDataStructure(FieldData[] fieldDataArray){
        //TODO field data array conversion
        return null;
    }

    public static FieldData[] convertToFieldArray(DataStructure dataStructure){
        //TODO DataStructure conversion
        return null;
    }

    public static DataStructureTableModel convertToDataStructureTableModel(DataStructure dataStructure){

        DataStructureTableModel tableModel = new DataStructureTableModel();
        for(int i = 0; i < dataStructure.size(); i++){
            Object value;

            if(dataStructure.types[i] == DataStructure.TYPE_OBJECT){
                value = convertToDataStructureTableModel((DataStructure)dataStructure.values[i]);
            }else{
                value = dataStructure.values[i];
            }

            FieldData fieldData = new FieldData(dataStructure.names[i], dataStructure.types[i], value);
            tableModel.addRow(new Object[]{i, fieldData.getTypeToString(), fieldData, fieldData.getValue()});
        }

        return tableModel;
    }
}
