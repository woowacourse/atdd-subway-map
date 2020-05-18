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
        this.id = id;
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
        if (stations.isEmpty()) {
            addFirstStation(lineStation);
            stations.add(lineStation);
            return;
        }
        if (Objects.isNull(lineStation.getPreStationId())) {
            addInFirst(lineStation);
            stations.add(lineStation);
            return;
        }
        if (isAddableInMiddleIfNewStation(lineStation)) {
            addInMiddleIfNewStation(lineStation);
            return;
        }
        if (isAddableInMiddleIfNewPreStation(lineStation)) {
            addInMiddleIfNewPreStation(lineStation);
            return;
        }
        addInEndLocation(lineStation);
    }

    private void addFirstStation(LineStation lineStation) {
        if (Objects.nonNull(lineStation.getPreStationId())) {
            stations.add(new LineStation(null, lineStation.getPreStationId(), 0, 0));
        }
    }

    private void addInFirst(LineStation lineStation) {
        stations.stream()
                .filter(station -> Objects.isNull(station.getPreStationId()))
                .findFirst()
                .ifPresent(station -> {
                    if (Objects.equals(lineStation.getStationId(), station.getStationId())) {
                        station.updatePreLineStation(lineStation.getPreStationId());
                        addFirstStation(lineStation);
                        return;
                    }
                    station.updatePreLineStation(lineStation.getStationId());
                });
    }

    private boolean isAddableInMiddleIfNewStation(LineStation lineStation) {
        return stations.stream()
                .anyMatch(station -> Objects.equals(lineStation.getPreStationId(), station.getPreStationId()));
    }

    private void addInMiddleIfNewStation(LineStation lineStation) {
        stations.stream()
                .filter(station -> Objects.equals(lineStation.getPreStationId(), station.getPreStationId()))
                .findFirst()
                .ifPresent(station -> {
                    station.updatePreLineStation(lineStation.getStationId());
                    stations.add(lineStation);
                });
    }

    private boolean isAddableInMiddleIfNewPreStation(LineStation lineStation) {
        return stations.stream()
                .anyMatch(station -> Objects.equals(lineStation.getStationId(), station.getStationId()));
    }

    private void addInMiddleIfNewPreStation(LineStation lineStation) {
        stations.stream()
                .filter(station -> Objects.equals(lineStation.getStationId(), station.getStationId()))
                .findFirst()
                .ifPresent(station -> {
                    Long newPreStationId = station.getPreStationId();
                    station.updatePreLineStation(lineStation.getPreStationId());
                    LineStation lineStationForSave = new LineStation(
                            newPreStationId, lineStation.getPreStationId(), lineStation.getDistance(), lineStation.getDuration());
                    stations.add(lineStationForSave);
                });
    }

    private void addInEndLocation(LineStation lineStation) {
        stations.stream()
                .filter(station -> Objects.equals(station.getStationId(), lineStation.getPreStationId()))
                .findFirst()
                .ifPresent(station -> stations.add(lineStation));
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
                .filter(station -> Objects.isNull(station.getPreStationId()))
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
}
