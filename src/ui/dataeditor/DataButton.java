package ui.dataeditor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DataButton extends JButton implements TableCellRenderer {

    private DataStructureTableModel objectData;


    public DataButton(DataStructureTableModel tableModel){
        super("Edit");

        this.objectData = tableModel;

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Window parent = SwingUtilities.windowForComponent(DataButton.this);
                JDialog window;
                window = new JDialog(parent, "A window", Dialog.ModalityType.APPLICATION_MODAL);
                window.setSize(600, 310);
                window.add(new FieldEditorPanel(objectData));
                window.setLocationRelativeTo(parent);
                window.setVisible(true);

                window.revalidate();
                window.repaint();

                //System.out.println("Clicked");
            }
        });

        //window.
    }

    public void setObjectData(DataStructureTableModel tableModel){
        this.objectData = tableModel;
    }

    public DefaultTableModel getObjectData(){
        return objectData;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(UIManager.getColor("Button.background"));
        }
        //setText((value == null) ? "" : value.toString());
        return this;
    }
}
