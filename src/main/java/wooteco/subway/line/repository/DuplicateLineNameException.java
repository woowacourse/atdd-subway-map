package wooteco.subway.line.repository;

public class DuplicateLineNameException extends LineRepositoryException {
    private static final String MESSAGE = "중복된 노선 이름입니다.";

    public DuplicateLineNameException() {
        super(MESSAGE);
    }
}
