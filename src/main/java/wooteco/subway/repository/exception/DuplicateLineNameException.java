package wooteco.subway.repository.exception;

public class DuplicateLineNameException extends IllegalArgumentException {

    public DuplicateLineNameException(String name) {
        super(String.format("[%s] 해당 이름의 지하철노선은 이미 존재합니다.", name));
    }
}
