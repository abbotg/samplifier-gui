package nzero.samplifier.gui.basic.hint;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings("Duplicates")
public class MouseOverHintManager implements MouseListener {
    private Map<Component, String> hintMap;
    private JLabel hintLabel;

    public MouseOverHintManager(JLabel hintLabel) {
        hintMap = new WeakHashMap<>();
        this.hintLabel = hintLabel;
    }

    public void addHintFor(Component comp, String hintText) {
        hintMap.put(comp, hintText);
    }


    public void enableHints(Component comp) {
        comp.addMouseListener(this);
        if (comp instanceof Container) {
            Component[] components = ((Container) comp).getComponents();
            for (Component component : components) {
                enableHints(component);
            }
        }
        if (comp instanceof MenuElement) {
            MenuElement[] elements = ((MenuElement) comp).getSubElements();
            for (MenuElement element : elements) {
                enableHints(element.getComponent());
            }
        }
    }


    private String getHintFor(Component comp) {
        String hint = hintMap.get(comp);
        if (hint == null) {
            if (comp instanceof JLabel)
                hint = hintMap.get(((JLabel) comp).getLabelFor());
            else if (comp instanceof JTableHeader)
                hint = hintMap.get(((JTableHeader) comp).getTable());
        }
        return hint;

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Component comp = (Component) e.getSource();
        String hint;
        do {
            hint = getHintFor(comp);
            comp = comp.getParent();
        } while ((hint == null) && (comp != null));
        if (hint != null)
            hintLabel.setText(hint);
    }


    @Override
    public void mouseExited(MouseEvent e) {
        hintLabel.setText(" ");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

}