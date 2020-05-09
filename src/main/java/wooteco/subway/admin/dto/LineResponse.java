package wooteco.subway.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;
    private int intervalTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String bgColor;

    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, LocalDateTime createdAt, LocalDateTime updatedAt, List<StationResponse> stations, String bgColor) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.stations = stations;
        this.bgColor = bgColor;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getStartTime(), line.getEndTime(), line.getIntervalTime(), line.getCreatedAt(), line.getUpdatedAt(), new ArrayList<>(), line.getBgColor());
    }

    public static LineResponse of(Line line, List<Station> stations) {
        return new LineResponse(line.getId(), line.getName(), line.getStartTime(), line.getEndTime(), line.getIntervalTime(), line.getCreatedAt(), line.getUpdatedAt(), StationResponse.listOf(stations), line.getBgColor());
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

    public List<StationResponse> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getBgColor() {
        return bgColor;
    }
}
