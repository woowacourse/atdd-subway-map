package wooteco.subway.station.exception;

public class InvalidStationNameException extends Station4XXException {
    public InvalidStationNameException(String message) {
        super(message);
    }
}
