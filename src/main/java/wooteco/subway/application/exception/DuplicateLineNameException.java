package wooteco.subway.application.exception;

public class DuplicateLineNameException extends DuplicateException {

    public DuplicateLineNameException(String name) {
        super(String.format("%s와 동일한 노선이 존재합니다.", name));
    }
}
