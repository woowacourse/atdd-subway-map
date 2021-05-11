package wooteco.subway.line.section;

public class NoneExistentStationException extends RuntimeException {

    public NoneExistentStationException(final String message) {
        super(message);
    }
}
