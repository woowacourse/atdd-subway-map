package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LineWithOrderedStationsResponse {
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String color;

    private List<Station> orderedStations;

    public LineWithOrderedStationsResponse() {}

    public LineWithOrderedStationsResponse(Long id, String name, LocalTime startTime, LocalTime endTime,
                        int intervalTime, LocalDateTime createdAt, LocalDateTime updatedAt,
                        String color, List<Station> orderedStations) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.color = color;
        this.orderedStations = orderedStations;
    }

    public static LineWithOrderedStationsResponse of(Line line, List<Station> orderedStations) {
        return new LineWithOrderedStationsResponse(line.getId(), line.getName(), line.getStartTime(),
                line.getEndTime(), line.getIntervalTime(), line.getCreatedAt(),
                line.getUpdatedAt(), line.getColor(), orderedStations);
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

    public List<Station> getOrderedStations() {
        return Collections.unmodifiableList(orderedStations);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
