package nzero.samplifier.gui;

public class InvalidInputLengthException extends Exception {
    private Object input;

    public InvalidInputLengthException(Object input) {
        super();
        this.input = input;
    }

    public InvalidInputLengthException(String message, Object input) {
        super(message);
        this.input = input;
    }

    public Object getInput() {
        return input;
    }
}
