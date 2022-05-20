package wooteco.subway.exception;

public class SubwayNotFoundException extends IllegalArgumentException {

    public SubwayNotFoundException(String message) {
        super(message);
    }
}
