package nzero.samplifier.gui;

import nzero.samplifier.model.Register;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainWindow {
    private List<Register> registers;
    private JFrame frame;
    private final JPanel cardPanel;
    private JPanel comboBoxPanel;
    private JComboBox<String> comboBox;

    public MainWindow(List<Register> registers) {
        this.registers = registers;
        cardPanel = new JPanel(new CardLayout());


        // Get register names for combo box
        String[] registerNames = new String[registers.size()];
        for (int i = 0; i < registers.size(); i++) {
            registerNames[i] = registers.get(i).getName();
        } // TODO: handle duplicate register names

        // create the combo box, add it to a JPanel
        comboBox = new JComboBox<>(registerNames);
        comboBox.setEditable(false);
        comboBox.addItemListener(e -> {
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, (String) e.getItem());
        });
        comboBoxPanel = new JPanel();
        comboBoxPanel.add(comboBox);
        comboBoxPanel.add(new JButton("Write"));

        // Create the "cards" in the card layout, which are RegisterPanels
        // and add them to the cardPanel. The cards (registers) are identified
        // by their name (a string)
        for (Register register : registers) {
            cardPanel.add(new RegisterPanel(register), register.getName()); // this is a deep call
        }

        frame = new JFrame("Samplifier GUI");

        frame.add(comboBoxPanel, BorderLayout.PAGE_START);
        frame.add(cardPanel, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

    }



}
