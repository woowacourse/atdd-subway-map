package wooteco.subway.exception;

public class DuplicatedStationNameException extends RuntimeException {

    public DuplicatedStationNameException(final String exceptionMessage) {
        super(exceptionMessage);
    }
}
