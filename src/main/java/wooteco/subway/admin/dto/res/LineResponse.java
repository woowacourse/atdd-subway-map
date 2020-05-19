package wooteco.subway.admin.dto.res;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

public class LineResponse {
    private Long id;
    private String title;
    private String backgroundColor;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String title, String backgroundColor, LocalTime startTime,
        LocalTime endTime, int intervalTime, LocalDateTime createdAt, LocalDateTime updatedAt,
        List<Station> stations) {
        this.id = id;
        this.backgroundColor = backgroundColor;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.stations = StationResponse.toStation(stations);
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getBackgroundColor(),
            line.getStartTime(), line.getEndTime(), line.getIntervalTime(), line.getCreatedAt(),
            line.getUpdatedAt(), new ArrayList<>());
    }

    public static LineResponse of(Line line, List<Station> stations) {
        return new LineResponse(line.getId(), line.getName(), line.getBackgroundColor(),
            line.getStartTime(), line.getEndTime(), line.getIntervalTime(), line.getCreatedAt(),
            line.getUpdatedAt(), stations);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBackgroundColor() {
        return backgroundColor;
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
