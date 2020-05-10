package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Line {
    @Id
    private Long id;
    private String name;
    private String color;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Set<LineStation> stations = new LinkedHashSet<>();

    public Line() {
    }

    public Line(Long id, String name, String color, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Line(String name, String color, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this(null, name, color, startTime, endTime, intervalTime);
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

    public void update(final Line line) {
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

    public void addLineStation(final LineStation lineStation) {
        final List<LineStation> listStations = new ArrayList<>(stations);

        if (listStations.isEmpty()) {
            listStations.add(lineStation);
            stations = new LinkedHashSet<>(listStations);
            return;
        }

        LineStation nextStation;
        if (lineStation.isFirstStation()) {
            nextStation = listStations.stream()
                    .filter(LineStation::isFirstStation)
                    .findFirst()
                    .orElse(null);

            updateAndInsertLineStation(lineStation, listStations, nextStation, 0);
        } else {
            LineStation beforeStation = listStations.stream()
                    .filter(station -> station.getStationId().equals(lineStation.getPreStationId()))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new);

            nextStation = getNextStation(beforeStation.getStationId(), listStations);

            updateAndInsertLineStation(lineStation, listStations, nextStation, listStations.indexOf(beforeStation) + 1);
        }
        stations = new LinkedHashSet<>(listStations);
    }

    private void updateAndInsertLineStation(final LineStation lineStation, final List<LineStation> listStations,
                                            final LineStation nextStation, final int index) {
        if (nextStation != null) {
            nextStation.updatePreLineStation(lineStation.getStationId());
        }
        listStations.add(index, lineStation);
    }

    public LineStation removeLineStationById(final Long stationId) {
        final List<LineStation> listStations = new ArrayList<>(stations);

        final LineStation lineStation = listStations.stream()
                .filter(station -> station.getStationId().equals(stationId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        LineStation nextStation;
        if (lineStation.isFirstStation()) {
            nextStation = listStations.get(1);
        } else {
            nextStation = getNextStation(stationId, listStations);
        }

        if (nextStation != null) {
            nextStation.updatePreLineStation(lineStation.getStationId());
        }

        listStations.remove(lineStation);
        stations = new LinkedHashSet<>(listStations);
        return lineStation;
    }

    private LineStation getNextStation(final Long stationId, final List<LineStation> listStations) {
        return listStations.stream()
                .filter(station -> !station.isFirstStation()
                        && station.getPreStationId().equals(stationId))
                .findFirst()
                .orElse(null);
    }

    public List<Long> getLineStationsId() {
        return stations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", intervalTime=" + intervalTime +
                ", stations=" + stations +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
