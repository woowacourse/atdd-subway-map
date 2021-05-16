package wooteco.subway.exception;

public class NotFoundException extends SubwayException {

    public NotFoundException() {
        super("해당 정보가 존재하지 않습니다.");
    }
}
