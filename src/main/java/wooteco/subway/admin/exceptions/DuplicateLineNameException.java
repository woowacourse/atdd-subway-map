package wooteco.subway.admin.exceptions;

public class DuplicateLineNameException extends IllegalArgumentException {
    private static final String message = "이름이 %s인 노선이 이미 존재합니다.";

    public DuplicateLineNameException(String name) {
        super(String.format(message, name));
    }
}
