package nzero.samplifier.gui.basic;

import nzero.samplifier.model.Register;

public interface RegisterPopOutWindow {

    void fireDataChange();

    boolean isVisible();

    Register getRegister();

}