package wooteco.subway.repository.exception;

public class DuplicateLineColorException extends IllegalArgumentException {

    public DuplicateLineColorException(String color) {
        super(String.format("[%s] 해당 색상의 지하철노선은 이미 존재합니다.", color));
    }
}
