package wooteco.subway.admin.dto.domain;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

public class LineDto {
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String lineColor;
    private List<LineStation> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LineDto(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String lineColor, List<LineStation> stations, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.lineColor = lineColor;
        this.stations = stations;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LineDto of(Line line) {
        return new LineDto(line.getId(), line.getName(), line.getStartTime(),
                line.getEndTime(), line.getIntervalTime(), line.getLineColor(),
                line.getStations(), line.getCreatedAt(), line.getUpdatedAt());
    }

    public Line toLine() {
        return new Line(name, startTime, endTime, intervalTime, lineColor);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public String getLineColor() {
        return lineColor;
    }

    public List<LineStation> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public static class LineDtoBuilder {
        private Long id;
        private String name;
        private LocalTime startTime;
        private LocalTime endTime;
        private int intervalTime;
        private String lineColor;
        private List<LineStation> stations = new LinkedList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public LineDtoBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public LineDtoBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public LineDtoBuilder setStartTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public LineDtoBuilder setEndTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public LineDtoBuilder setIntervalTime(int intervalTime) {
            this.intervalTime = intervalTime;
            return this;
        }

        public LineDtoBuilder setLineColor(String lineColor) {
            this.lineColor = lineColor;
            return this;
        }

        public LineDtoBuilder setStations(List<LineStation> stations) {
            this.stations = stations;
            return this;
        }

        public LineDtoBuilder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public LineDtoBuilder setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public LineDto build() {
            return new LineDto(this.id, this.name, this.startTime,
                    this.endTime, this.intervalTime, this.lineColor,
                    this.stations, this.createdAt, this.updatedAt);
        }
    }
}
