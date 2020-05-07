package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Table("Line")
public class Line {
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private Set<LineStation> stations = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String color;

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String color) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.color = color;
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String color) {
        this(null, name, startTime, endTime, intervalTime, color);
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

    public Set<LineStation> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void update(Line line) {
        if (line.getName() != null) {
            this.name = line.getName();
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
        this.stations.add(lineStation);
    }

    public void removeLineStationById(Long stationId) {
        this.stations.removeIf(station->station.isStationId(stationId));
    }

    public List<Long> getLineStationsId() {
        return stations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
    }

    public String getColor() {
        return color;
    }
}
