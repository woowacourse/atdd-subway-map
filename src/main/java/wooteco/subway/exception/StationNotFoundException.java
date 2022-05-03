package wooteco.subway.exception;

public class StationNotFoundException extends RuntimeException {

    private static final String MESSAGE = "존재하지 않는 역입니다.";

    @Override
    public String getMessage() {
        return MESSAGE;
    }

}
