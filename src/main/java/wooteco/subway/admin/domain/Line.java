package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;

import wooteco.subway.admin.exception.LineStationNotFoundException;

public class Line {
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String bgColor;
    private List<LineStation> stations = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime,
        String bgColor) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.bgColor = bgColor;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime,
        String bgColor) {
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

    public String getBgColor() {
        return bgColor;
    }

    public List<LineStation> getStations() {
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
        if (line.getBgColor() != null) {
            this.bgColor = line.getBgColor();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation lineStation) {
        stations.add(lineStation);
    }

    public void addLineStation(int index, LineStation lineStation) {
        stations.add(index, lineStation);
    }

    public void removeLineStationById(Long stationId) {
        LineStation stationToRemove = stations.stream()
            .filter(lineStation -> lineStation.getStationId().equals(stationId))
            .findFirst()
            .orElseThrow(() -> new LineStationNotFoundException(stationId));
        if (stationToRemove.getPreStationId() == null) {
            nextOf(stationToRemove).ifPresent(
                lineStation -> lineStation.updatePreLineStation(null));
            stations.remove(stationToRemove);
            return;
        }
        nextOf(stationToRemove).ifPresent(
            lineStation -> lineStation.updatePreLineStation(stationToRemove.getPreStationId()));
        stations.remove(stationToRemove);
    }

    private Optional<LineStation> nextOf(LineStation station) {

        try {
            return Optional.of(stations.get(stations.indexOf(station) + 1));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    private Optional<LineStation> previousOf(LineStation station) {
        try {
            return Optional.of(stations.get(stations.indexOf(station) - 1));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    public List<Long> getLineStationsId() {
        return stations
            .stream()
            .map(LineStation::getStationId)
            .collect(Collectors.toList());
    }
}
