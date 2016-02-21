package org.arthur.salesman.errors;

/**
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class FilePatternException extends RuntimeException {

    public FilePatternException() {
        super("The file is not formatted with the required pattern");
    }

    public FilePatternException(final String message) {
        super(message);
    }

}
