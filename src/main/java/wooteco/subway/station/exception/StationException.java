package wooteco.subway.station.exception;

public class StationException extends RuntimeException {
    private StationError stationError;

    public StationException(StationError stationError) {
        super(stationError.getMessage());
        this.stationError = stationError;
    }

    public int statusCode() {
        return stationError.getStatusCode();
    }
}
