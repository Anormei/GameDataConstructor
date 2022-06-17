package ui.dataeditor;

import dataconstructor.FieldData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewPanel extends JPanel {

    private JTable fieldTable;
    private JScrollPane scrollPane;
    private DataStructureTableModel fieldListModel;

    public ViewPanel(DataStructureTableModel fieldListModel) {
        this.fieldListModel = fieldListModel;

        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();

        fieldTable = new JTable(fieldListModel);
        fieldTable.getTableHeader().setReorderingAllowed(false);
        fieldTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ButtonEditor buttonEditor = new ButtonEditor(new JCheckBox(), new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int row = fieldTable.getSelectedRow();
                int column = fieldTable.getSelectedColumn();

                Object value = fieldListModel.getValueAt(row, column);

                //System.out.println("Clicked");
                if (value instanceof DefaultTableModel) {
                    Window parent = SwingUtilities.windowForComponent(ViewPanel.this);

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

        fieldTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        fieldTable.getColumnModel().getColumn(3).setCellEditor(buttonEditor);

        fieldTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        scrollPane = new JScrollPane(fieldTable);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 1;
        c.weighty = 1;

        add(scrollPane, c);
    }
}
