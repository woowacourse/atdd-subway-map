package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class LineResponse {
    private Long id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String bgColor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Set<Station> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String title, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor, LocalDateTime createdAt, LocalDateTime updatedAt, Set<Station> stations) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.bgColor = bgColor;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getStartTime(), line.getEndTime(), line.getIntervalTime(), line.getBgColor(), line.getCreatedAt(), line.getUpdatedAt(), new HashSet<>());
    }

    public static LineResponse withStations(Line line, Set<Station> stations) {
        LineResponse of = of(line);
        of.stations = stations;
        return of;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
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

    public Set<Station> getStations() {
        return stations;
    }

    public String getBgColor() {
        return bgColor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
