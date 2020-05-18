package wooteco.subway.admin.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

public class LineResponse {
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String color;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

	private List<Station> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, LocalTime startTime, LocalTime endTime,
        int intervalTime, String color, LocalDateTime createdAt, LocalDateTime updatedAt,
		List<Station> stations) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.color = color;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.stations = stations;
    }

	public static LineResponse of(Line line, List<Station> stations) {
        return new LineResponse(line.getId(), line.getName(), line.getStartTime(),
            line.getEndTime(), line.getIntervalTime(), line.getColor(), line.getCreatedAt(),
            line.getUpdatedAt(), stations);
    }

	public static List<LineResponse> listOf(Map<Line, List<Station>> lineWithStations) {
        return lineWithStations.entrySet().stream()
            .map(lineSetEntry -> LineResponse.of(lineSetEntry.getKey(), lineSetEntry.getValue()))
            .collect(Collectors.toList());
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

    public String getColor() {
        return color;
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
