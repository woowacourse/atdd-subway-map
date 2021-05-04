package wooteco.subway.exception;

public class DuplicatedNameException extends RuntimeException{

    public DuplicatedNameException() {
        super("이미 존재하는 이름입니다.");
    }
}
