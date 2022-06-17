package ui;

import dataconstructor.DataStructure;
import dataconstructor.DataUtils;
import ui.dataeditor.DataStructureTableModel;
import ui.dataeditor.FieldEditorPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.List;

public class GUI extends JFrame {

    private JTabbedPane tabbedPane;

    private DataEditorPanel gameDataPanel;
    //private JPanel classFormatPanel;
    private CollisionPanel collisionDataPanel;

    public GUI(){
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        //int width = gd.getDisplayMode().getWidth();
        //int height = gd.getDisplayMode().getHeight();.


        setSize(600, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Game Data Constructor");

        tabbedPane = new JTabbedPane();

        gameDataPanel = new DataEditorPanel();
        //classFormatPanel = new JPanel();
        collisionDataPanel = new CollisionPanel();

        setJMenuBar(gameDataPanel.menuBar());

        tabbedPane.addTab("Data Editor", gameDataPanel);
        //tabbedPane.addTab("Class Formatter", classFormatPanel);
        tabbedPane.addTab("Collision Conversion", collisionDataPanel);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                //TODO change menu
                int index = tabbedPane.getSelectedIndex();

                if(index == 0){
                    setJMenuBar(gameDataPanel.menuBar());
                }
            }
        });

        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }


}
