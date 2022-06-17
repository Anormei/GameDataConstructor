package ui.dataeditor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

public class ButtonRenderer extends JButton implements TableCellRenderer {

    private final DefaultTableCellRenderer renderer;

    public ButtonRenderer(){
        renderer = new DefaultTableCellRenderer();
        setText("View");
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof TableModel) {
            //System.out.println("row = " + row + ", column = " + column);
            return this;
        }
        return component;
    }
}

