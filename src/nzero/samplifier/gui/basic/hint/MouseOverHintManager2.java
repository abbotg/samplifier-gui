package nzero.samplifier.gui.basic.hint;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

@SuppressWarnings("Duplicates")
public class MouseOverHintManager2 implements MouseListener {
    private Map<Component, Supplier<String>> hintMap;
    private JLabel hintLabel;

    public MouseOverHintManager2(JLabel hintLabel) {
        hintMap = new WeakHashMap<>();
        this.hintLabel = hintLabel;
    }

    public void addHintFor(Component comp, Supplier<String> runnable) {
        hintMap.put(comp, runnable);
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


    private boolean getHintFor(Component comp) {
        Supplier<String> hint = hintMap.get(comp);
        if (hint != null) {
            hintLabel.setText(hint.get());
            return true;
        }
        return false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Component comp = (Component) e.getSource();
        boolean foundHint;
        do {
            foundHint = getHintFor(comp);
            comp = comp.getParent();
        } while (!foundHint && comp != null);
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