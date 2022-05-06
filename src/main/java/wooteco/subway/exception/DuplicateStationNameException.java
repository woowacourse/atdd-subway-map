package wooteco.subway.exception;

public class DuplicateStationNameException extends ElementAlreadyExistException {

    public DuplicateStationNameException() {
        super("[ERROR] 이미 존재하는 역 이름입니다.");
    }
}
