package wooteco.subway.application.exception;

public class DuplicateStationNameException extends DuplicateException {

    public DuplicateStationNameException(String name) {
        super(String.format("%s와 동일한 이름의 지하철역이 있습니다.", name));
    }
}
