package wooteco.subway.admin.exceptions;

public class DuplicationNameException extends RuntimeException {
    public DuplicationNameException(final String lineName) {
        super(String.format("중복된 이름입니다. : %s", lineName));
    }
}
