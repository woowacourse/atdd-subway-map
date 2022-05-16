package wooteco.subway.exception;

public class CanNotDeleteException extends IllegalInputException {

    public CanNotDeleteException() {
        super("[ERROR] 삭제 할 수 없습니다.");
    }
}
