package wooteco.subway.exception;

public class DuplicatedStationException extends RuntimeException {

    private static final String MESSAGE = "이미 존재하는 역 이름입니다.";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
