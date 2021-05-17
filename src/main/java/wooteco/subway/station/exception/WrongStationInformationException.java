package wooteco.subway.station.exception;

public class WrongStationInformationException extends Station4XXException {
    public WrongStationInformationException(String message) {
        super(message);
    }
}
