package nzero.samplifier.util;

import javax.swing.*;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        throwable.printStackTrace();

        String message = String.format("There was an error in the Samplifier GUI:%n%s%nCrash? (Otherwise, continue in an inconsistent state)",
                throwable.toString());

        int n = JOptionPane.showConfirmDialog(null, message, "Error", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            System.exit(1);
        }
    }
}
