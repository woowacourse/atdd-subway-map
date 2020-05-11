package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Line {
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String bgColor;
    private Set<LineStation> stations = new LinkedHashSet<>();
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
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.bgColor = bgColor;
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
        if (lineStation == null) {
            throw new NullPointerException("지하철역이 없슴니다.");
        }

        stations.stream()
                .filter(existLineStation -> Objects.equals(existLineStation.getPreStationId()
                        , lineStation.getPreStationId()))
                .findFirst()
                .ifPresent(existLineStation -> existLineStation.updatePreLineStation(
                        lineStation.getStationId()));
        stations.add(lineStation);
    }

    public void removeLineStationById(Long stationId) {
        LineStation deleteTarget = stations.stream()
                .filter(station -> station.getStationId().equals(stationId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
        stations.stream()
                .filter(station -> stationId.equals(station.getPreStationId()))
                .findFirst()
                .ifPresent(station -> station.updatePreLineStation(deleteTarget.getPreStationId()));
        stations.remove(deleteTarget);
    }

    public List<Long> findLineStationsId() {
        List<Long> linesStationsId = new ArrayList<>();
        if (stations.isEmpty()) {
            return linesStationsId;
        }
        LineStation startStation = stations.stream()
                .filter(station -> station.getPreStationId() == null)
                .findFirst()
                .orElseThrow(RuntimeException::new);

        linesStationsId.add(startStation.getStationId());

        while (linesStationsId.size() != stations.size()) {
            linesStationsId.add(stations.stream()
                    .filter(station -> Objects.equals(linesStationsId.get(linesStationsId.size() - 1),
                            station.getPreStationId()))
                    .findFirst()
                    .map(LineStation::getStationId)
                    .orElseThrow(NoSuchElementException::new));
        }
        return linesStationsId;
    }
}
