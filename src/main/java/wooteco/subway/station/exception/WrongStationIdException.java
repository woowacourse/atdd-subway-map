package wooteco.subway.station.exception;

public class WrongStationIdException extends Station4XXException {
    public WrongStationIdException(String message) {
        super(message);
    }
}
