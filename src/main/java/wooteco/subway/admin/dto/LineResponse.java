package wooteco.subway.admin.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

public class LineResponse {
    private Long id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String bgColor;

    private Set<Station> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String title, LocalTime startTime, LocalTime endTime, int intervalTime,
        LocalDateTime createdAt, LocalDateTime updatedAt, String bgColor,
        Set<Station> stations) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.bgColor = bgColor;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getTitle(), line.getStartTime(), line.getEndTime(),
            line.getIntervalTime(), line.getCreatedAt(), line.getUpdatedAt(), line.getBgColor(), new HashSet<>());
    }

    public static LineResponse of(Line line, Set<Station> stations) {
        return new LineResponse(line.getId(), line.getTitle(), line.getStartTime(), line.getEndTime(),
            line.getIntervalTime(), line.getCreatedAt(), line.getUpdatedAt(), line.getBgColor(), stations);
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

    public Set<Station> getStations() {
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

    @Override
    public String toString() {
        return "LineResponse{" +
            "stations=" + stations.toString() +
            '}';
    }
}
