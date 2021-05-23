package wooteco.subway.exception;

public class StationDuplicationException extends RuntimeException {

    private static final String message = "이미 등록된 역입니다.";

    public StationDuplicationException() {
        super(message);
    }
}
