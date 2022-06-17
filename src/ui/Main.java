package ui;

import dataconstructor.DataStructure;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
	// write your code here
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){

        }

        new GUI();
    }
}
