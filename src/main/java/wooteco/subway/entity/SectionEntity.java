package wooteco.subway.entity;

public class SectionEntity {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public static class Builder {

        private Long id;
        private final Long lineId;
        private final Long upStationId;
        private final Long downStationId;
        private final int distance;

        public Builder(Long lineId, Long upStationId, Long downStationId, int distance) {
            this.lineId = lineId;
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public SectionEntity build() {
            return new SectionEntity(this);
        }
    }

    private SectionEntity(Builder builder) {
        this.id = builder.id;
        this.lineId = builder.lineId;
        this.upStationId = builder.upStationId;
        this.downStationId = builder.downStationId;
        this.distance = builder.distance;
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
}
