package wooteco.subway.admin.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

public class LineResponse {
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String bgColor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Station> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, LocalTime startTime, LocalTime endTime,
        int intervalTime, String bgColor, LocalDateTime createdAt, LocalDateTime updatedAt,
        List<Station> stations) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.bgColor = bgColor;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getStartTime(),
            line.getEndTime(), line.getIntervalTime(), line.getBgColor(), line.getCreatedAt(),
            line.getUpdatedAt(), new ArrayList<>());
    }

    public static LineResponse of(Line line, List<Station> stations) {
        return new LineResponse(line.getId(), line.getName(), line.getStartTime(),
            line.getEndTime(), line.getIntervalTime(), line.getBgColor(), line.getCreatedAt(),
            line.getUpdatedAt(), stations);
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

    public List<Station> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
