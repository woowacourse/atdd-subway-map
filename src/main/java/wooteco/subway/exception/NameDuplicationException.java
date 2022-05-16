package wooteco.subway.exception;

public class NameDuplicationException extends IllegalArgumentException {
    public NameDuplicationException() {
        super("중복된 이름입니다.");
    }
}
