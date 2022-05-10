package wooteco.subway.exception;

public class DuplicateLineNameException extends ElementAlreadyExistException {

    public DuplicateLineNameException() {
        super("[ERROR] 이미 존재하는 노선 이름입니다.");
    }
}
