package ui.dataeditor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;

public class ButtonEditor extends DefaultCellEditor{

    private JButton button;
    private Object value;
    private DefaultTableCellRenderer renderer;
    private JDialog window;

    private ActionListener actionListener;

    public ButtonEditor(JCheckBox checkBox, ActionListener actionListener) {
        super(checkBox);
        this.actionListener = actionListener;
        renderer = new DefaultTableCellRenderer();
        button = new JButton("View");
        button.setOpaque(true);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                fireEditingStopped();
                actionListener.actionPerformed(e);

            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if(!(value instanceof TableModel)){
            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }
        //JButton button = (JButton)value;
        this.value = value;
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }

        return button;
    }

    @Override
    public Object getCellEditorValue(){
        return value;
    }

    @Override
    protected void fireEditingStopped(){
        super.fireEditingStopped();
    }
}
