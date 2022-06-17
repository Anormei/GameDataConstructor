package ui.dataeditor;

import dataconstructor.DataStructure;
import dataconstructor.FieldData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class FieldEditorPanel extends JPanel implements TableUpdater {

    private JScrollPane fieldScrollPane;
    private JTable fieldTable;
    private JButton removeFieldButton;
    private JButton addFieldButton;

    private ButtonEditor buttonEditor;
    private ButtonRenderer buttonRenderer;

    //private int hoveredRow = -1;
    //private int hoveredColumn = -1;

    private DataStructureTableModel fieldListModel;

    private ValueEditor valueEditor;

    int selectedRowIndex;

    private boolean saved;

    public FieldEditorPanel(DataStructureTableModel tableModel){

        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();

        fieldListModel = tableModel;
        if(tableModel == null) {
            fieldListModel = new DataStructureTableModel();
            saved = true;
        }

        fieldTable = new JTable(fieldListModel);
        fieldTable.getTableHeader().setReorderingAllowed(false);
        fieldTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fieldTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedRowIndex = fieldTable.getSelectedRow();

                if(selectedRowIndex != -1){
                    valueEditor.setVisible(true);
                    valueEditor.setData(selectedRowIndex, (FieldData)fieldListModel.getValueAt(selectedRowIndex, 2));
                }else{
                    valueEditor.setVisible(false);
                    valueEditor.setData(-1, null);
                }
            }
        });

        buttonEditor = new ButtonEditor(new JCheckBox(), new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int row = fieldTable.getSelectedRow();
                int column = fieldTable.getSelectedColumn();

                Object value = fieldListModel.getValueAt(row, column);

                //System.out.println("Clicked");
                if (value instanceof DefaultTableModel) {
                    Window parent = SwingUtilities.windowForComponent(FieldEditorPanel.this);

                    String name = fieldListModel.getValueAt(row, 2).toString();

                    JDialog window;
                    window = new JDialog(parent, name, Dialog.ModalityType.MODELESS);
                    window.setSize(300, 310);
                    window.add(new ViewPanel((DataStructureTableModel) value));
                    window.setLocationRelativeTo(parent);
                    window.setVisible(true);

                    window.revalidate();
                    window.repaint();

                }
            }
        });

        buttonRenderer = new ButtonRenderer();

        fieldTable.getColumnModel().getColumn(3).setCellRenderer(buttonRenderer);
        fieldTable.getColumnModel().getColumn(3).setCellEditor(buttonEditor);

        fieldTable.getColumnModel().getColumn(0).setPreferredWidth(50);


        fieldScrollPane = new JScrollPane(fieldTable);

        valueEditor = new ValueEditor(this);

        removeFieldButton = new JButton("-");
        removeFieldButton.setFont(new Font(removeFieldButton.getFont().getName(), Font.PLAIN, 20));
        addFieldButton = new JButton("+");
        addFieldButton.setFont(new Font(removeFieldButton.getFont().getName(), Font.PLAIN, 20));

        removeFieldButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                removeField();
            }

        });

        addFieldButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                addField();
            }

        });

        valueEditor.setVisible(false);

        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 10 ,0);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 0.5;
        c.weighty = 1.0;
        add(fieldScrollPane, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(0, 10, 10 ,0);
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 0.5;
        c.weighty = 1.0;

        add(valueEditor, c);

        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 0,0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.25;
        c.weighty = 0;

        add(removeFieldButton, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.25;
        c.weighty = 0;

        add(addFieldButton, c);
    }


    public void setTableModel(DataStructureTableModel model){
        this.fieldListModel = model;
        fieldTable.setModel(model);

        fieldTable.getColumnModel().getColumn(3).setCellRenderer(buttonRenderer);
        fieldTable.getColumnModel().getColumn(3).setCellEditor(buttonEditor);

        fieldTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        saved = true;
    }

    private void addField(){
        FieldData fieldData = new FieldData();
        fieldListModel.addRow(new Object[]{fieldListModel.getRowCount(), fieldData.getTypeToString(), fieldData, fieldData.getValue()});
    }

    private void removeField() {
        int index = fieldTable.getSelectedRow();

        if (index != -1) {
            fieldListModel.removeRow(index);
            for(int i = 0; i < fieldListModel.getRowCount(); i++){
                fieldListModel.setValueAt(i, i, 0);
            }
        }
    }

    @Override
    public int getSelectedRowIndex(){
        return selectedRowIndex;
    }

    @Override
    public void updateTable(int index, FieldData data) {
        //System.out.println("Updated");

        fieldListModel.setValueAt(data.getTypeToString(), index, 1);
        fieldListModel.setValueAt(data.getValue(), index, 3);
        fieldListModel.fireTableRowsUpdated(index, index);

        saved = false;

        //fieldTable.revalidate();
        //fieldTable.repaint();
    }

    public DataStructureTableModel getTable(){
        saved = true;
        return fieldListModel;
    }

    public List<String> checkDataValidation(){
        int size = fieldListModel.getRowCount();
        List<String> invalidFields = new ArrayList<>();
        for(int i = 0; i < size; i++){
            FieldData fieldData = (FieldData)fieldListModel.getValueAt(i, 2);
            /*if(fieldData.getName().length() == 0){
                invalidFields.add("Field [" + i + "] has empty name!");
            }*/

            if(fieldData.getType() == -1) {
                invalidFields.add("Field [" + i + "] has no assigned type!");
            }
        }
        return invalidFields;
    }

    public boolean isSaved(){
        return saved;
    }
}
