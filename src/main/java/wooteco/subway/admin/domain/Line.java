package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Table("Line")
public class Line {
    public static final long PRE_ID_OF_FIRST_STATION = -1L;

    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private Set<LineStation> lineStations = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String color;

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String color) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.color = color;
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String color) {
        this(null, name, startTime, endTime, intervalTime, color);
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

    public Set<LineStation> getLineStations() {
        return lineStations;
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

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation lineStation) {
        this.lineStations.stream()
                .filter(anyLineStation -> anyLineStation.isPreStationId(lineStation.getPreStationId()))
                .findFirst()
                .ifPresent(
                        aLineStation -> {
                            updatePreOfLineStation(aLineStation.getStationId(), lineStation.getStationId());
                        });
        this.lineStations.add(lineStation);
    }

    public void removeLineStationByStationId(Long stationId) {
        this.lineStations.stream()
                .filter(lineStation -> lineStation.isPreStationId(stationId))
                .findFirst()
                .ifPresent(nextStationId -> {
                    this.lineStations.stream()
                            .filter(lineStation -> lineStation.isStationId(stationId))
                            .findFirst()
                            .ifPresent(preStation ->
                                    updatePreOfLineStation(nextStationId.getStationId(),preStation.getPreStationId()));
                });
        this.lineStations.removeIf(lineStation -> lineStation.isStationId(stationId));
    }

    private void updatePreOfLineStation(Long stationId, Long newPreStationId) {
        this.lineStations.stream()
                .filter(lineStation -> lineStation.isStationId(stationId))
                .forEach(lineStation -> lineStation.updatePreStationId(newPreStationId));
    }

    public List<Long> getStationIds() {
        Map<Long, Long> stationIdOrder = new HashMap<>();    // key: 전 역 ID, value: 현재 역 ID
        List<Long> orderedStations = new ArrayList<>();

        makeStationIdOrder(stationIdOrder);

        if (stationIdOrder.isEmpty()) {
            return orderedStations;
        }

        Long now = PRE_ID_OF_FIRST_STATION;
        for (int i = 0; i < stationIdOrder.size(); i++) {
            now = stationIdOrder.get(now);
            orderedStations.add(now);
        }
        return Collections.unmodifiableList(orderedStations);
    }

    private void makeStationIdOrder(Map<Long, Long> stationIdOrder) {
        lineStations.forEach(lineStation -> {
            if (Objects.isNull(lineStation.getPreStationId())) {
                stationIdOrder.put(PRE_ID_OF_FIRST_STATION, lineStation.getStationId());
            } else {
                stationIdOrder.put(lineStation.getPreStationId(), lineStation.getStationId());
            }
        });
    }

    public String getColor() {
        return color;
    }
}
