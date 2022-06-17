package ui.dataeditor;

import javax.swing.table.DefaultTableModel;

public class DataStructureTableModel extends DefaultTableModel {

    private static final String[] COLUMN_NAMES = {"Index", "Type", "Name", "Value"};

    public DataStructureTableModel(){
        super();
        setRowCount(0);
        setColumnCount(4);
        setColumnIdentifiers(COLUMN_NAMES);
    }

    public DataStructureTableModel(int rowCount, int columnCount){
        super(rowCount, columnCount);

    }

    public DataStructureTableModel(Object[][] data, Object[] columnNames){
        super(data, columnNames);
    }

    public DataStructureTableModel(Object[] columnNames, int rowCount){
        super(columnNames, rowCount);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        Object value = getValueAt(row, column);
        return value instanceof DefaultTableModel;
    }

}
