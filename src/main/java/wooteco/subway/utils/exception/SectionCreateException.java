package wooteco.subway.utils.exception;

public class SectionCreateException extends SubwayException {

    public SectionCreateException(String message) {
        super("[ERROR] 구간을 생성할 수 없습니다. cause: " + message);
    }
}
