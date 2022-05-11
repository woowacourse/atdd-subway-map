package wooteco.subway.exception;

public abstract class ElementAlreadyExistException extends IllegalStateException {

    public ElementAlreadyExistException(final String s) {
        super(s);
    }
}
