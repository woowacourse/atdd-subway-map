package wooteco.subway.admin.dto.domain;

public class LineStationDto {
    private Long stationId;
    private Long preStationId;
    private int distance;
    private int duration;

    public LineStationDto(Long stationId, Long preStationId, int distance, int duration) {
        this.stationId = stationId;
        this.preStationId = preStationId;
        this.distance = distance;
        this.duration = duration;
    }

    public Long getStationId() {
        return stationId;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    public static class LineStationBuilder {
        private Long stationId;
        private Long preStationId;
        private int distance;
        private int duration;

        public LineStationBuilder setStationId(Long stationId) {
            this.stationId = stationId;
            return this;
        }

        public LineStationBuilder setPreStationId(Long preStationId) {
            this.preStationId = preStationId;
            return this;
        }

        public LineStationBuilder setDistance(int distance) {
            this.distance = distance;
            return this;
        }

        public LineStationBuilder setDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public LineStationDto build() {
            return new LineStationDto(this.stationId, this.preStationId, this.distance, this.duration);
        }
    }
}
