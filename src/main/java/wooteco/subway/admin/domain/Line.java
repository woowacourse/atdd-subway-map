package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.util.StringUtils;

import wooteco.subway.admin.exception.InvalidStationInsertionException;
import wooteco.subway.admin.exception.LineStationNotFoundException;

public class Line {
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String bgColor;
    private List<LineStation> stations = new LinkedList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime,
        String bgColor) {
        this.id = id;
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

    public void update(Line line) {
        if (!StringUtils.isEmpty(line.getName())) {
            this.name = line.getName();
        }
        if (!StringUtils.isEmpty(line.getStartTime())) {
            this.startTime = line.getStartTime();
        }
        if (!StringUtils.isEmpty(line.getEndTime())) {
            this.endTime = line.getEndTime();
        }
        if (line.getIntervalTime() != 0) {
            this.intervalTime = line.getIntervalTime();
        }
        if (!StringUtils.isEmpty(line.getBgColor())) {
            this.bgColor = line.getBgColor();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation newLineStation) {
        if (newLineStation.isFirst()) {
            stations.stream()
                .filter(LineStation::isFirst)
                .findFirst()
                .ifPresent(station -> station.updatePreLineStation(newLineStation.getStationId()));
            stations.add(0, newLineStation);
            return;
        }
        LineStation preStation = stations.stream()
            .filter(station -> station.isPreviousOf(newLineStation))
            .findFirst()
            .orElseThrow(
                () -> new InvalidStationInsertionException(newLineStation.getPreStationId()));

        Optional<LineStation> maybeNextStation = nextOf(preStation);
        maybeNextStation.ifPresent(
            station -> station.updatePreLineStation(newLineStation.getStationId()));

        stations.add(stations.indexOf(preStation) + 1, newLineStation);
    }

    public void removeLineStationById(Long stationId) {
        LineStation target = stations.stream()
            .filter(lineStation -> lineStation.getStationId().equals(stationId))
            .findFirst()
            .orElseThrow(() -> new LineStationNotFoundException(stationId));

        Long preStationId = target.getPreStationId();

        nextOf(target).ifPresent(lineStation -> lineStation.updatePreLineStation(preStationId));
        stations.remove(target);
    }

    private Optional<LineStation> nextOf(LineStation station) {
        try {
            return Optional.of(stations.get(stations.indexOf(station) + 1));
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
}
