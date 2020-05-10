package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import wooteco.subway.admin.exception.NotFoundLineStationException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Line {
    @Id
    private Long id;
    private String name;
    private String color;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private Set<LineStation> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(String name, String color, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this.name = name;
        this.color = color;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.stations = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
        stations.stream()
                .filter(station -> Objects.equals(station.getPreStationId(), lineStation.getPreStationId()))
                .findFirst()
                .ifPresent(station -> station.updatePreLineStation(lineStation.getStationId()));
        stations.add(lineStation);
    }

    public void removeLineStationById(Long stationId) {
        LineStation lineStation = stations.stream()
                .filter(station -> Objects.equals(station.getStationId(), stationId))
                .findFirst()
                .orElseThrow(NotFoundLineStationException::new);

        stations.stream()
                .filter(station -> Objects.equals(station.getPreStationId(), stationId))
                .findFirst()
                .ifPresent(station -> station.updatePreLineStation(lineStation.getPreStationId()));
        stations.remove(lineStation);
    }

    public List<Long> getLineStationsId() {
        List<Long> lineStationsId = new ArrayList<>();

        if (stations.isEmpty()) {
            return lineStationsId;
        }

        LineStation currentLineStation = findStartLineStation();

        while (Objects.nonNull(currentLineStation)) {
            Long currentLineStationId = currentLineStation.getStationId();
            lineStationsId.add(currentLineStationId);
            currentLineStation = findNextLineStationByStationId(currentLineStationId);
        }

        return lineStationsId;
    }

    private LineStation findStartLineStation() {
        return stations.stream()
                .filter(station -> Objects.isNull(station.getPreStationId()))
                .findFirst()
                .orElseThrow(NotFoundLineStationException::new);
    }

    private LineStation findNextLineStationByStationId(Long stationId) {
        return stations.stream()
                .filter(station -> Objects.equals(station.getPreStationId(), stationId))
                .findFirst()
                .orElse(null);
    }
}
