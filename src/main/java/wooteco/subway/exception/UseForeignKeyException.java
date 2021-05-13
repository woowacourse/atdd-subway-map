package wooteco.subway.exception;

public class UseForeignKeyException extends RuntimeException {

    private static final String MESSAGE = "[ERROR] 현재 사용중인 아이템이어서 삭제할 수 없습니다.";

    public UseForeignKeyException() {
        super(MESSAGE);
    }
}
