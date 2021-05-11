package wooteco.subway.line.exception;

public class LineException extends RuntimeException {
    private LineError lineError;

    public LineException(LineError lineError) {
        super(lineError.getMessage());
        this.lineError = lineError;
    }

    public int statusCode() {
        return lineError.getStatusCode();
    }
}
