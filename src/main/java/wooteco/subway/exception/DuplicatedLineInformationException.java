package wooteco.subway.exception;

public class DuplicatedLineInformationException extends RuntimeException {
    private static final String MESSAGE = "중복되는 라인 정보가 존재합니다.";

    public DuplicatedLineInformationException() {
        super(MESSAGE);
    }
}
