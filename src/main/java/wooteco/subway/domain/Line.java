package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    private Long id;
    private Name name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;


    public Line(String name,
                String color,
                final Long upStationId,
                final Long downStationId,
                final int distance) {
        this(null, name, color, upStationId, downStationId, distance);
    }

    public Line(Long id,
                final String name,
                final String color,
                final Long upStationId,
                final Long downStationId,
                final int distance) {
        Objects.requireNonNull(name, ERROR_NULL);
        Objects.requireNonNull(color, ERROR_NULL);
        Objects.requireNonNull(upStationId, ERROR_NULL);
        Objects.requireNonNull(downStationId, ERROR_NULL);
        Objects.requireNonNull(distance, ERROR_NULL);
        this.id = id;
        this.name = new Name(name);
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Line line = (Line) o;

        if (getDistance() != line.getDistance()) {
            return false;
        }
        if (getId() != null ? !getId().equals(line.getId()) : line.getId() != null) {
            return false;
        }
        if (getName() != null ? !getName().equals(line.getName()) : line.getName() != null) {
            return false;
        }
        if (getColor() != null ? !getColor().equals(line.getColor()) : line.getColor() != null) {
            return false;
        }
        if (getUpStationId() != null ? !getUpStationId().equals(line.getUpStationId())
            : line.getUpStationId() != null) {
            return false;
        }
        return getDownStationId() != null ? getDownStationId().equals(line.getDownStationId())
            : line.getDownStationId() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getColor() != null ? getColor().hashCode() : 0);
        result = 31 * result + (getUpStationId() != null ? getUpStationId().hashCode() : 0);
        result = 31 * result + (getDownStationId() != null ? getDownStationId().hashCode() : 0);
        result = 31 * result + getDistance();
        return result;
    }
}
