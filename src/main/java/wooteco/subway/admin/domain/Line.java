package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("LINE")
public class Line {
    @Id
    private Long id;
    private String name;
    private String backgroundColor;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private Set<LineStation> stations = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Line() {
    }

    public Line(Long id, String name, String backgroundColor, LocalTime startTime,
        LocalTime endTime,
        int intervalTime) {
        this.name = name;
        this.backgroundColor = backgroundColor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBackgroundColor() {
        return backgroundColor;
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
        if (line.getBackgroundColor() != null) {
            this.backgroundColor = line.getBackgroundColor();
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

    public void addLineStation(LineStation newLineStation) {
        List<Long> ids = this.findLineStationsId();
        if (isLineStationEmpty(ids) || isNewLineStationLastLineStation(newLineStation, ids)) {
            stations.add(newLineStation);
            return;
        }
        if (newLineStation.isStartStation()) {
            stations.stream()
                .filter(LineStation::isStartStation)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("출발역이 존재하지 않습니다."))
                .updatePreStationIdWithIdOf(newLineStation);
            stations.add(newLineStation);
            return;
        }
        stations.stream()
            .filter(newLineStation::hasSamePreStationIdWith)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("해당하는 구간이 없습니다."))
            .updatePreStationIdWithIdOf(newLineStation);
        stations.add(newLineStation);
    }

    public void removeLineStationById(Long stationId) {
        LineStation stationToBeRemoved = stations.stream()
            .filter(station -> stationId.equals(station.getStationId()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("해당하는 지하철 역이 없습니다."));
        Long previousId = stationToBeRemoved.getPreStationId();
        stations.remove(stationToBeRemoved);

        stations.stream()
            .filter(station -> stationId.equals(station.getPreStationId()))
            .findFirst()
            .ifPresent(station -> station.updatePreLineStation(previousId));
    }

    public List<Long> findLineStationsId() {
        List<Long> ids = new ArrayList<>();
        if (stations.size() == 0) {
            return Collections.emptyList();
        }
        stations.stream()
            .filter(LineStation::isStartStation)
            .findFirst()
            .map(station -> ids.add(station.getStationId()));

        while (ids.size() != stations.size()) {
            stations.stream()
                .filter(station -> isNewLineStationLastLineStation(station, ids))
                .findFirst()
                .map(station -> ids.add(station.getStationId()));
        }
        return ids;
    }

    private boolean isLineStationEmpty(List<Long> ids) {
        return ids.size() == 0;
    }

    private boolean isNewLineStationLastLineStation(LineStation newLineStation, List<Long> ids) {
        return ids.get(ids.size() - 1).equals(newLineStation.getPreStationId());
    }
}
