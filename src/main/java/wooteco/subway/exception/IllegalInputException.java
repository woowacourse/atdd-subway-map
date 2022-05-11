package wooteco.subway.exception;

public abstract class IllegalInputException extends IllegalArgumentException {

    public IllegalInputException(final String s) {
        super(s);
    }
}
