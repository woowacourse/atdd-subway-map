package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import wooteco.subway.admin.exception.DuplicatedLineStationException;
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
        checkExistLineStation(lineStation);

        if (lineStation.isNotStarting()) {
            findLineStationByStationId(lineStation.getPreStationId())
                    .orElseThrow(NotFoundLineStationException::new);
        }

        updatePreLineStation(lineStation.getPreStationId(), lineStation.getStationId());
        stations.add(lineStation);
    }

    private void checkExistLineStation(LineStation lineStation) {
        findLineStationByStationId(lineStation.getStationId())
                .ifPresent(station -> {
                    if (station.isEqualToPreStationId(lineStation.getPreStationId())) {
                        throw new DuplicatedLineStationException(station);
                    }
                });
    }

    public void removeLineStationById(Long stationId) {
        LineStation lineStation = findLineStationByStationId(stationId)
                        .orElseThrow(NotFoundLineStationException::new);

        updatePreLineStation(stationId, lineStation.getPreStationId());
        stations.remove(lineStation);
    }

    private void updatePreLineStation(Long oldId, Long newId) {
        findNextLineStationByStationId(oldId)
                .ifPresent(station -> station.updatePreLineStation(newId));
    }

    private Optional<LineStation> findLineStationByStationId(Long stationId) {
        return stations.stream()
                .filter(station -> station.isEqualToStationId(stationId))
                .findFirst();
    }

    public List<Long> getLineStationsId() {
        List<Long> lineStationsId = new ArrayList<>();
        Optional<LineStation> maybeLineStation = findStartLineStation();

        while (maybeLineStation.isPresent()) {
            Long stationId = maybeLineStation.get().getStationId();
            lineStationsId.add(stationId);
            maybeLineStation = findNextLineStationByStationId(stationId);
        }

        return lineStationsId;
    }

    private Optional<LineStation> findStartLineStation() {
        return stations.stream()
                .filter(LineStation::isStarting)
                .findFirst();
    }

    private Optional<LineStation> findNextLineStationByStationId(Long stationId) {
        return stations.stream()
                .filter(station -> station.isEqualToPreStationId(stationId))
                .findFirst();
    }
}
