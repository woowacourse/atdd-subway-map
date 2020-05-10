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
    private String bgColor;
    private Set<LineStation> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.bgColor = bgColor;
        this.stations = new LinkedHashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public String getBgColor() {
        return bgColor;
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
        if (line.getBgColor() != null) {
            this.bgColor = line.getBgColor();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation newLineStation) {
        if (isLineStationWithoutPreStation(newLineStation)) {
            sortLineStations(newLineStation);
            return;
        }

        if (isLineStationWithPreStation(newLineStation)) {
            Set<LineStation> savedLineStations = this.stations;
            this.stations = new LinkedHashSet<>();

            for (LineStation lineStation : savedLineStations) {
                if (isLineStationWithoutPreStation(lineStation)) {
                    this.stations.add(lineStation);
                } else if (isNotSamePreStation(newLineStation, lineStation)) {
                    this.stations.add(lineStation);
                } else {
                    this.stations.add(newLineStation);
                    lineStation.updatePreLineStation(newLineStation.getStationId());
                    this.stations.add(lineStation);
                }
            }
        }

        this.stations.add(newLineStation);
    }

    private boolean isLineStationWithoutPreStation(LineStation lineStation) {
        return lineStation.getPreStationId() == null;
    }

    private boolean isLineStationWithPreStation(LineStation newLineStation) {
        return newLineStation.getPreStationId() != null;
    }

    private boolean isNotSamePreStation(LineStation newLineStation, LineStation lineStation) {
        return !lineStation.getPreStationId().equals(newLineStation.getPreStationId());
    }

    private void sortLineStations(LineStation newLineStation) {
        Set<LineStation> savedLineStations = this.stations;
        this.stations = new LinkedHashSet<>();
        this.stations.add(newLineStation);
        this.stations.addAll(savedLineStations);
    }

    public void removeLineStationById(Long stationId) {
        for (LineStation lineStation : stations) {
            if (lineStation.getStationId().equals(stationId)) {
                stations.remove(lineStation);
                break;
            }
        }
    }

    public List<Long> getLineStationsId() {
        return stations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
    }
}
