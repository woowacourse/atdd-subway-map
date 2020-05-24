package wooteco.subway.admin.domain.exception;

public class DuplicatedLineException extends SubwayException {
    public DuplicatedLineException() {
        super("이미 중복된 이름의 노선이 존재합니다.");
    }
}
