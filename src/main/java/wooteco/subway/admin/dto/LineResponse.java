package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String bgColor;

    private List<Station> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String title, LocalTime startTime, LocalTime endTime, int intervalTime, LocalDateTime createdAt, LocalDateTime updatedAt, List<Station> stations, String bgColor) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.stations = stations;
        this.bgColor = bgColor;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getTitle(), line.getStartTime(), line.getEndTime(),
                line.getIntervalTime(), line.getCreatedAt(), line.getUpdatedAt(), new LinkedList<>(), line.getBgColor());
    }

    public static LineResponse of(Line line, List<Station> stations) {
        return new LineResponse(line.getId(), line.getTitle(), line.getStartTime(), line.getEndTime(),
                line.getIntervalTime(), line.getCreatedAt(), line.getUpdatedAt(), stations, line.getBgColor());
    }

    public static List<LineResponse> listOf(List<Line> lines) {
        return lines.stream()
                .map(it -> LineResponse.of(it))
                .collect(Collectors.toList());
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
