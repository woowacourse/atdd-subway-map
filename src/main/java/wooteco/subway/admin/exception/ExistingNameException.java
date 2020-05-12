package wooteco.subway.admin.exception;

public class ExistingNameException extends IllegalArgumentException {
    public ExistingNameException(String name) {
        super(name + " : 이미 존재하는 이름입니다.");
    }
}
