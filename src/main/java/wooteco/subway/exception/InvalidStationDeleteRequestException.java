package wooteco.subway.exception;

public class InvalidStationDeleteRequestException extends IllegalArgumentException {

    public InvalidStationDeleteRequestException(String message) {
        super(message);
    }

}
