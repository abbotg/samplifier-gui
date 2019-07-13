/*
 * fman - The file attribute manager
 *
 * Copyright (c) 2018 by Gunther Abbot - All Rights Reserved
 */

package nzero.samplifier.gui;

import javax.swing.*;

public class ConfirmationDialog {

    private ConfirmationDialog() {
    }

    public static boolean isConfirmed(String message) {
        return display(message);
    }

    public static boolean isConfirmed() {
        return display("Are you sure?");
    }


    private static boolean display(String message) {
        @SuppressWarnings("UnusedAssignment") int n = -1;
        n = JOptionPane.showOptionDialog(null,
                message,
                "Alert",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                null
        );
        //cancel returns 2
        //ok returns 0
        return n == 0;
    }
}
