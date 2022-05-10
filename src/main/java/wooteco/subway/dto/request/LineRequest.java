package wooteco.subway.dto.request;

import java.util.Objects;
import wooteco.subway.domain.Line;

public class LineRequest {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    private String name;
    private String color;

    private LineRequest() {
    }

    public LineRequest(String name, String color) {
        Objects.requireNonNull(name, ERROR_NULL);
        Objects.requireNonNull(color, ERROR_NULL);
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line toEntity(final Long id) {
        return new Line(id, this.name, this.color);
    }

    public Line toEntity() {
        return toEntity(null);
    }
}
