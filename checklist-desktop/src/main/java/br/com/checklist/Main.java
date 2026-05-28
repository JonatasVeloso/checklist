package br.com.checklist;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChecklistFrame frame = new ChecklistFrame();
            frame.setVisible(true);
        });
    }
}