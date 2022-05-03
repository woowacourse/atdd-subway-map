package wooteco.subway.exception;

public class DuplicatedLineException extends RuntimeException {

    private static final String MESSAGE = "노선 이름 혹은 노선 색이 이미 존재합니다.";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
