package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Line {
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private Set<LineStation> stations;
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
        this.stations = new LinkedHashSet<>();
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this(null, name, startTime, endTime, intervalTime, bgColor);
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

    public String getBgColor() {
        return bgColor;
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

    public void addLineStation(LineStation newLineStation) {
        Set<LineStation> newLineStations = new LinkedHashSet<>();
        for (LineStation lineStation : stations) {
            addToNewLineStations(lineStation, newLineStation, newLineStations);
        }

        if (notAddedYet(newLineStations)) {
            newLineStations.add(newLineStation);
        }
        stations = newLineStations;
    }

    private void addToNewLineStations(LineStation lineStation, LineStation newLineStation, Set<LineStation> newStations) {
        if (shouldAdd(newLineStation, lineStation)) {
            newStations.add(newLineStation);
            newStations.add(lineStation);
            lineStation.updatePreLineStation(newLineStation.getStationId());
            return;
        }
        newStations.add(lineStation);
    }

    private boolean shouldAdd(LineStation newLineStation, LineStation lineStation) {
        Long newLinePreStationId = newLineStation.getPreStationId();
        if (newLinePreStationId == null || newLinePreStationId == 0) {
            return true;
        }
        return newLinePreStationId.equals(lineStation.getPreStationId());
    }

    private boolean notAddedYet(Set<LineStation> newStations) {
        return stations.size() == newStations.size()
                || stations.size() == 0;
    }

    public void removeLineStationById(Long stationId) {
        Set<LineStation> newStations = new LinkedHashSet<>();
        for (LineStation station : stations) {
            Long prevLineStationId = addLineStationIfNotTarget(stationId, station, newStations);
            checkAndUpdateLink(station, prevLineStationId, stationId);
        }
        stations = newStations;
    }

    private Long addLineStationIfNotTarget(Long stationId, LineStation station, Set<LineStation> newStations) {
        if (!station.getStationId().equals(stationId)) {
            newStations.add(station);
        }
        return station.getStationId();
    }

    private void checkAndUpdateLink(LineStation station, Long prevLineStationId, Long stationId) {
        if (stationId.equals(prevLineStationId)) {
            station.updatePreLineStation(prevLineStationId);
        }
    }

    public List<Long> getLineStationsId() {
        return stations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
    }
}
