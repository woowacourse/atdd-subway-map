package wooteco.subway.admin.domain.exception;

public class DuplicationNameException extends IllegalArgumentException {
    public DuplicationNameException(String title) {
        super("중복된 이름입니다 -" + title);
    }
}
