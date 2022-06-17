package ui.dataeditor;

import dataconstructor.FieldData;

public interface TableUpdater{
        void updateTable(int index, FieldData data);
        int getSelectedRowIndex();
    }