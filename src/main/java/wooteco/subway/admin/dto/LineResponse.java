package wooteco.subway.admin.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import wooteco.subway.admin.domain.line.Line;
import wooteco.subway.admin.domain.station.Station;

public class LineResponse {
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String bgColor;
    private Set<Station> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LineResponse(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor, Set<Station> stations, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.bgColor = bgColor;
        this.stations = stations;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LineResponse of(Line line, Set<Station> stations) {
        return new LineResponse(line.getId(), line.getName(), line.getStartTime(), line.getEndTime(), line.getIntervalTime(), line.getBgColor(), stations, line.getCreatedAt(), line.getUpdatedAt());
    }

    public static LineResponse of(Line line) {
        return of(line, new HashSet<>());
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

    public String getBgColor() {
        return bgColor;
    }

    public Set<Station> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
