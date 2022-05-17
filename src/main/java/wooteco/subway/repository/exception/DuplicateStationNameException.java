package wooteco.subway.repository.exception;

public class DuplicateStationNameException extends IllegalArgumentException {

    public DuplicateStationNameException(String name) {
        super(String.format("[%s] 해당 이름의 지하철역은 이미 존재합니다.", name));
    }

}
