package wooteco.subway.domain;

import wooteco.subway.dto.LineRequest;
import wooteco.subway.exception.ClientException;

public class Line {

    private final Long id;
    private final String name;
    private final String color;

    public Line(long id, String name, String color) {
        validateNull(name, color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this(0L, name, color);
    }

    private void validateNull(String name, String color) {
        if (name.isBlank() || color.isBlank()) {
            throw new ClientException("지하철 노선의 이름과 색을 모두 입력해주세요.");
        }
    }

    public boolean isSameName(String target) {
        return name.equals(target);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
