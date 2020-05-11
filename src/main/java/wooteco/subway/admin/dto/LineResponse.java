package wooteco.subway.admin.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import wooteco.subway.admin.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private int intervalTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, LocalTime startTime, LocalTime endTime,
        int intervalTime,
        LocalDateTime createdAt, LocalDateTime updatedAt, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.stations = stations;
    }

    public static LineResponse of(Line line, List<StationResponse> stations) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getStartTime(),
            line.getEndTime(),
            line.getIntervalTime(), line.getCreatedAt(), line.getUpdatedAt(), stations);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
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

    public List<StationResponse> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
