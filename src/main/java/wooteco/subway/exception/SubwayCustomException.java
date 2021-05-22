package wooteco.subway.exception;

public class SubwayCustomException extends RuntimeException {

    private final SubwayException subwayException;

    public SubwayCustomException(SubwayException subwayException) {
        super(subwayException.message());
        this.subwayException = subwayException;
    }

    public String message() {
        return subwayException.message();
    }

    public int status() {
        return subwayException.status();
    }
}
