package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;

public class Line {
    private static final Long DEPARTURE_VALUE = null;
    private static final int DEFAULT_DISTANCE = 10;
    private static final int DEFAULT_DURATION = 10;

    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String backgroundColor;
    private List<LineStation> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime,
        String backgroundColor) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.backgroundColor = backgroundColor;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.stations = new LinkedList<>();
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String backgroundColor) {
        this(null, name, startTime, endTime, intervalTime, backgroundColor);
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

    public String getBackgroundColor() {
        return backgroundColor;
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
        if (line.getBackgroundColor() != null) {
            this.backgroundColor = line.getBackgroundColor();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation newLineStation) {
        if (stations.isEmpty() && isNotStartStation(newLineStation)) {
            stations.add(new LineStation(null, newLineStation.getPreStationId(), DEFAULT_DISTANCE, DEFAULT_DURATION));
        } else if (!stations.isEmpty()) {
            stations.stream()
                .filter(lineStation -> Objects.equals(lineStation.getPreStationId(), newLineStation.getPreStationId()))
                .findAny()
                .ifPresent(lineStation -> lineStation.updatePreLineStation(newLineStation.getStationId()));
        }
        stations.add(newLineStation);
    }

    public void removeLineStationById(Long stationId) {
        LineStation lineStation = stations.stream()
            .filter(station -> Objects.equals(station.getStationId(), stationId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("해당하는 station 정보가 없습니다."));

        if (!isNotStartStation(lineStation)) {
            updateLineStation(lineStation, DEPARTURE_VALUE);
            return;
        }

        updateLineStation(lineStation, lineStation.getPreStationId());
    }

    private void updateLineStation(final LineStation lineStation, final Long updateValue) {
        stations.stream()
            .filter(station -> Objects.equals(lineStation.getStationId(), station.getPreStationId()))
            .findFirst()
            .ifPresent(station -> station.updatePreLineStation(updateValue));

        stations.remove(lineStation);
    }

    private boolean isNotStartStation(LineStation newlineStation) {
        return Objects.nonNull(newlineStation.getPreStationId());
    }

    private Optional<LineStation> findHeadStation() {
        return stations.stream()
            .filter(lineStation -> Objects.isNull(lineStation.getPreStationId()))
            .findFirst();
    }

    private Optional<LineStation> findPreStationBy(Long stationId) {
        return stations.stream()
            .filter(lineStation -> Objects.equals(lineStation.getPreStationId(), stationId))
            .findFirst();
    }

    private List<LineStation> sortByStationRule() {
        List<LineStation> lineStations = new LinkedList<>();

        Optional<LineStation> headLineStation = findHeadStation();

        if (!headLineStation.isPresent()) {
            return lineStations;
        }

        Long nextStationId = headLineStation.get().getStationId();
        lineStations.add(headLineStation.get());

        while (true) {
            Optional<LineStation> lineStation = findPreStationBy(nextStationId);
            if (!lineStation.isPresent()) {
                break;
            }
            lineStations.add(lineStation.get());
            nextStationId = lineStation.get().getStationId();
        }

        return lineStations;
    }

    public List<Long> getLineStationsId() {
        List<LineStation> lineStations = sortByStationRule();

        return lineStations.stream()
            .map(LineStation::getStationId)
            .collect(Collectors.toList());
    }
}
