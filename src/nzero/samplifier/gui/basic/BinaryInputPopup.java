package nzero.samplifier.gui.basic;

import nzero.samplifier.gui.GUIUtils;
import nzero.samplifier.gui.SamplifierMainWindow;
import nzero.samplifier.model.BitMap;
import nzero.samplifier.model.DataType;
import nzero.samplifier.model.Register;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Modal
 * <><></></>
 */
public class BinaryInputPopup {

    /*
     * Order matters everywhere
     */
    private static final String HELP_STRING = "<html>Esc/Enter: save and close" +
            "<br>c: clear" +
            "<br>Tab: traverse bits" +
            "<br>Mouse over to view bit number</html>";
    private static final Color ONE_COLOR = Color.LIGHT_GRAY, ZERO_COLOR = Color.white;

    private Register register;
    private BitMap bitMap;
    private SamplifierMainWindow parent;
    private List<JTextField> textFields;
    private JDialog dialog;


    private BinaryInputPopup(Register register, BitMap bitMap, SamplifierMainWindow parent) {
        this.register = register;
        this.bitMap = bitMap;
        this.parent = parent;

        this.textFields = new ArrayList<>(bitMap.getLength());

        assert bitMap.getDataType() == DataType.BIN;

        JPanel buttonPanel = new JPanel(new FlowLayout());

        for (int i = bitMap.getMsb(); i >= bitMap.getLsb(); i--) {
            JTextField textField = createCustomTextField((register.getData() & (1 << i)) >= 1, i);
            textFields.add(textField);
            buttonPanel.add(textField);
        }

        dialog = new JDialog(parent.getFrame(), "Input", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(buttonPanel, BorderLayout.CENTER);
//        dialog.add(GUIUtils.createButton("Save", this::saveAndCloseButton), BorderLayout.PAGE_END);
        JLabel label = new JLabel(HELP_STRING, JLabel.CENTER);
        label.setFont(new Font(Font.DIALOG, Font.ITALIC, 11));
        dialog.add(label, BorderLayout.PAGE_END);

        dialog.setUndecorated(true);
        dialog.pack();
        dialog.setLocationRelativeTo(textFields.get(0));
        dialog.setVisible(true);

    }

    public void saveAndCloseButton(ActionEvent e) {
        StringBuilder builder = new StringBuilder();
        for (JTextField textField : textFields) {
            String text = textField.getText().trim();
            if (text.isEmpty()) {
                builder.append('0');
            } else {
                builder.append(text);
            }
        }
        assert builder.toString().length() == bitMap.getLength();
        register.setData(bitMap, Integer.parseUnsignedInt(builder.toString(), 2));

        SwingUtilities.invokeLater(() -> {
            dialog.setVisible(false);
            dialog.dispose();
        });

        parent.fireWriteRegistersDataChange();
    }

    public void clearFields(ActionEvent e) {
        for (JTextField textField : textFields) {
            textField.setText("0");
        }
        textFields.get(0).requestFocus();
    }

    public static void display(Register register,
                               BitMap bitMap,
                               SamplifierMainWindow parent) {
        if (register.isWritable())
            new BinaryInputPopup(register, bitMap, parent);
    }

    private JTextField createCustomTextField(boolean initialVal, int bitNumber) {
        JTextField textField = new JTextField( 2);
        textField.setDocument(new JTextFieldLimit(1));
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case KeyEvent.VK_ESCAPE:
                    case KeyEvent.VK_ENTER:
                        saveAndCloseButton(null);
                        e.consume();
                        return;
                    case 'c':
                        clearFields(null);
                        e.consume();
                        return;
                    case '0':
                    case '1':
                    case KeyEvent.VK_BACK_SPACE:
                    case KeyEvent.VK_DELETE:
                        return; // pass these through the filter
                    default:
                        e.consume(); // do not pass others
                }
            }
        });
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                textField.selectAll();
            }
        });
        GUIUtils.addChangeListener(textField, e -> {
            String text = textField.getText();
            try {
                textField.setBackground(Integer.parseUnsignedInt(text) == 1 ? ONE_COLOR : ZERO_COLOR);
                if (text.length() == 1) {
                    textField.transferFocus();
                }
            } catch (NumberFormatException ignored) {

            }
        });
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.selectAll();
            }
        });
//        NavigationFilter filter = new NavigationFilter() {
//
//            @Override
//            public void setDot(FilterBypass fb, int dot, Position.Bias bias) {
//                if (dot >= 1) {
//                    fb.setDot(0, bias);
//                    textField.transferFocus();
//                } else {
//                    fb.setDot(dot, bias);
//                }
//            }
//
//            @Override
//            public void moveDot(FilterBypass fb, int dot, Position.Bias bias) {
//                if (dot >= 1) {
//                    fb.setDot(0, bias);
//                    textField.transferFocus();
//                } else {
//                    fb.moveDot(dot, bias);
//                }
//            }
//
//        };
//        textField.setNavigationFilter(filter);
//        textField.addActionListener();
        textField.setText(initialVal ? "1" : "0");
        textField.setBackground(initialVal ? ONE_COLOR : ZERO_COLOR);
        textField.setHorizontalAlignment(JTextField.CENTER);
//        textField.setOpaque(false);
        textField.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
//        textField.setBorder(BorderFactory.createEmptyBorder());
        textField.setToolTipText(String.valueOf(bitNumber));
        return textField;
    }

    private static class JTextFieldLimit extends PlainDocument {

        private int limit;

        JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }

        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null)
                return;

            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }
}
