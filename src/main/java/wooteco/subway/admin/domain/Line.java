package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public class Line {
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    @Embedded.Empty
    private LineStations stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String bgColor;

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.bgColor = bgColor;
        this.stations = new LineStations(new LinkedHashSet<>());
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this(null, name, startTime, endTime, intervalTime, bgColor);
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
        if (line.getBgColor() != null) {
            this.bgColor = line.getBgColor();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation lineStation) {
        stations.addLineStation(lineStation);
    }

    public void removeLineStationById(Long stationId) {
        stations.removeLineStationById(stationId);
    }

    public List<Long> getLineStationsId() {
        return stations.getLineStationsId();
    }

    public List<Station> createSortedStations(List<Station> stations) {
        List<Station> newStations = new ArrayList<>();
        for (Long id : getLineStationsId()) {
            addByLineStationID(stations, newStations, id);
        }
        return newStations;
    }

    private void addByLineStationID(List<Station> stations, List<Station> newStations, Long id) {
        stations.stream()
                .filter(station -> Objects.equals(station.getId(), id))
                .findAny()
                .ifPresent(newStations::add);
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

    public LineStations getStations() {
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
