package wooteco.subway.line.section;

import java.util.Objects;
import wooteco.subway.exception.ValidationFailureException;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    private Section(final Builder builder) {
        validateDifferentStation(builder.upStationId, builder.downStationId);
        this.id = builder.id;
        this.lineId = builder.lineId;
        this.upStationId = builder.upStationId;
        this.downStationId = builder.downStationId;
        this.distance = builder.distance;
    }

    public static Builder Builder() {
        return new Builder();
    }

    private void validateDifferentStation(final Long upStationId, final Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new ValidationFailureException("구간은 상행역과 하행역이 같을 수 없습니다.");
        }
    }

    public void validateSmaller(final int distance) {
        if (this.distance <= distance) {
            throw new ValidationFailureException("새로 추가할 구간의 거리가 기존 구간의 거리보다 크거나 같으면 안 됩니다.");
        }
    }

    public Section createUpdatedSection(final Long upStationId, final Long downStationId, final int distance) {
        if (this.upStationId.equals(upStationId)) {
            return Section.Builder()
                .id(id)
                .lineId(lineId)
                .upStationId(downStationId)
                .downStationId(this.downStationId)
                .distance(this.distance - distance)
                .build();
        }
        return Section.Builder()
            .id(id)
            .lineId(lineId)
            .upStationId(this.upStationId)
            .downStationId(upStationId)
            .distance(this.distance - distance)
            .build();
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects
            .equals(lineId, section.lineId) && Objects.equals(upStationId, section.upStationId) && Objects
            .equals(downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStationId, downStationId, distance);
    }

    public static class Builder {

        public Long id;
        private Long lineId;
        private Long upStationId;
        private Long downStationId;
        private int distance;

        private Builder() {
        }

        public Builder id(final Long id) {
            this.id = id;
            return this;
        }

        public Builder lineId(final Long lineId) {
            this.lineId = lineId;
            return this;
        }

        public Builder upStationId(final Long upStationId) {
            this.upStationId = upStationId;
            return this;
        }

        public Builder downStationId(final Long downStationId) {
            this.downStationId = downStationId;
            return this;
        }

        public Builder distance(final int distance) {
            this.distance = distance;
            return this;
        }

        public Section build() {
            return new Section(this);
        }
    }
}
