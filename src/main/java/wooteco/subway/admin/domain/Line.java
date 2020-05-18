package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;

import wooteco.subway.admin.utils.Validator;

public class Line {
    @Id
    private Long id;
    private String name;
    private String color;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    @Embedded.Nullable
    LineStations lineStations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, String color, LocalTime startTime, LocalTime endTime,
        int intervalTime) {
        Validator.validateNotEmpty(name);
        Validator.validateNotContainsBlank(name);
        this.name = name;
        this.color = color;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.lineStations = new LineStations(new LinkedList<>());
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Line(String name, String color, LocalTime startTime, LocalTime endTime,
        int intervalTime) {
        this(null, name, color, startTime, endTime, intervalTime);
    }

    public void update(Line line) {
        if (line.getName() != null) {
            this.name = line.getName();
        }
        if (line.getColor() != null) {
            this.color = line.getColor();
        }
        if (line.getStartTime() != null) {
            this.startTime = line.getStartTime();
        }
        if (line.getEndTime() != null) {
            this.endTime = line.getEndTime();
        }
        if (line.getIntervalTime() != 0) {
            this.intervalTime = line.getIntervalTime();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation lineStation) {
        lineStations.add(lineStation);
    }

    public void removeLineStationById(Long stationId) {
        lineStations.removeLineStationById(stationId);
    }

    public List<Long> makeLineStationsIds() {
        return lineStations.makeLineStationsIds();
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

    public LineStations getStations() {
        return lineStations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
