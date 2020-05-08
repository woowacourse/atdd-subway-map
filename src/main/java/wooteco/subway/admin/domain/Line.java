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

    public void addLineStation(LineStation lineStation) {
        Set<LineStation> newStations = new LinkedHashSet<>();
        int updated = 0;
        for (LineStation station : stations) {
            if (lineStation.getPreStationId() == null) {
                updated = 1;
                newStations.add(lineStation);
                newStations.add(station);
                station.updatePreLineStation(lineStation.getStationId());
                continue;
            }
            if (lineStation.getPreStationId().equals(station.getPreStationId())) {
                updated = 1;
                newStations.add(lineStation);
                newStations.add(station);
                station.updatePreLineStation(lineStation.getStationId());
                continue;
            }
            newStations.add(station);
        }
        if (updated == 0) {
            newStations.add(lineStation);
        }
        if (stations.size() == 0) {
            newStations.add(lineStation);
        }
        stations = newStations;
    }

    public void removeLineStationById(Long stationId) {
        LineStation lineStation = stations.stream()
                .filter(station -> station.isSameId(stationId))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
        stations.remove(lineStation);
    }

    public List<Long> getLineStationsId() {
        return stations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
    }
}
