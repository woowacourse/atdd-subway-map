package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class Line {
    @Id
    private Long id;
    private String name;
    private String color;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    @MappedCollection(idColumn = "line", keyColumn = "index")
    private List<LineStation> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, String color, LocalTime startTime, LocalTime endTime,
        int intervalTime) {
        this.name = name;
        this.color = color;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.stations = new LinkedList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Line(String name, String color, LocalTime startTime, LocalTime endTime,
        int intervalTime) {
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
        validateHavingSame(lineStation);
        if (Objects.isNull(lineStation.getPreStationId())) {
            stations.add(0, lineStation);
            if (stations.size() != 1) {
                LineStation firstLineStation = stations.get(1);
                firstLineStation.updatePreLineStation(lineStation.getStationId());
            }
            return;
        }
        validateLineStation(lineStation);
        int insertIndex = findPreStationIndex(lineStation.getPreStationId()) + 1;
        if (stations.size() != insertIndex) {
            LineStation existing = stations.get(insertIndex);
            existing.updatePreLineStation(lineStation.getStationId());
        }
        stations.add(insertIndex, lineStation);
    }

    private void validateLineStation(LineStation lineStation) {
        if (lineStation.getPreStationId().equals(lineStation.getStationId())) {
            throw new IllegalArgumentException("같은 역을 출발지점과 도착지점으로 정할 수 없습니다.");
        }
    }

    public void removeLineStationById(Long stationId) {
        int index = findPreStationIndex(stationId);
        if (stations.size() != index + 1) {
            LineStation lineStation = stations.get(index);
            stations.get(index + 1).updatePreLineStation(lineStation.getPreStationId());
        }
        stations.remove(index);
    }

    public List<Long> getLineStationsId() {
        LinkedList<Long> stations = new LinkedList<>();
        for (LineStation lineStation : this.stations) {
            stations.add(lineStation.getStationId());
        }
        return new ArrayList<>(stations);
    }

    private void validateHavingSame(LineStation lineStation) {
        for (LineStation station : stations) {
            if (Objects.isNull(station.getPreStationId())) {
                continue;
            }
            if (station.isSameStation(lineStation)) {
                throw new IllegalArgumentException("이미 등록된 구간입니다.");
            }
        }
    }

    private int findPreStationIndex(Long stationId) {
        for (int i = 0; i < stations.size(); i++) {
            if (stations.get(i).isBaseStation(stationId)) {
                return i;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 이전역입니다.");
    }

}
