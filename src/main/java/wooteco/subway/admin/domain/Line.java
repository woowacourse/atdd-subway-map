package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.MappedCollection;

import wooteco.subway.admin.exception.LineStationException;

public class Line {
    @Id
    private Long id;
    private String name;
    private String color;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    @MappedCollection(idColumn = "line", keyColumn = "index")
    private LinkedList<LineStation> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PersistenceConstructor
    public Line(Long id, String name, String color, LocalTime startTime, LocalTime endTime,
        int intervalTime) {
        this.id = id;
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
        validateLineStation(lineStation);
        int insertIndex = findInsertIndex(lineStation.getPreStationId());
        Long stationId = lineStation.getStationId();
        updatePreStation(insertIndex, stationId);
        stations.add(insertIndex, lineStation);
    }

    public void removeLineStationById(Long stationId) {
        int index = findStationIndex(stationId);
        int indexToUpdate = index + 1;
        Long preStationdId = stations.get(index).getPreStationId();
        updatePreStation(indexToUpdate, preStationdId);
        stations.remove(index);
    }

    public List<Long> getLineStationsId() {
        LinkedList<Long> stations = new LinkedList<>();
        for (LineStation lineStation : this.stations) {
            stations.add(lineStation.getStationId());
        }
        return stations;
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

    private void validateLineStation(LineStation lineStation) {
        validateStation(lineStation);
        validateStations(lineStation);
        validateAlreadyRegistered(lineStation);
        validateHavingSame(lineStation);
    }

    private void validateStation(LineStation lineStation) {
        if (Objects.isNull(lineStation.getStationId())) {
            throw new LineStationException("현재역은 비어있을 수 없습니다.");
        }
    }

    private void validateAlreadyRegistered(LineStation lineStation) {
        if (!lineStation.isFirstLineStation() && stations.isEmpty()) {
            throw new LineStationException("첫 노선을 먼저 등록해야 합니다.");
        }
    }

    private void validateStations(LineStation lineStation) {
        if (lineStation.getStationId().equals(lineStation.getPreStationId())) {
            throw new LineStationException("같은 역을 출발지점과 도착지점으로 정할 수 없습니다.");
        }
    }

    private void validateHavingSame(LineStation lineStation) {
        for (LineStation station : stations) {
            if (station.isSameStation(lineStation)) {
                throw new LineStationException("이미 등록된 구간입니다.");
            }
        }
    }

    private void updatePreStation(int index, Long stationId) {
        if (stations.size() != index) {
            LineStation existing = stations.get(index);
            existing.updatePreLineStation(stationId);
        }
    }

    private int findInsertIndex(Long preStationId) {
        if (Objects.isNull(preStationId)) {
            return 0;
        }
        LineStation last = stations.getLast();
        if (preStationId.equals(last.getStationId())) {
            return stations.size();
        }
        LineStation preStation = stations.stream()
            .filter(station -> station.samePreStation(preStationId))
            .findAny()
            .orElseThrow(() -> new LineStationException("현재 노선에 등록되지 않은 이전역입니다."));
        return stations.indexOf(preStation);
    }

    private int findStationIndex(Long stationId) {
        LineStation lineStation = stations.stream()
            .filter(station -> station.isBaseStation(stationId))
            .findFirst()
            .orElseThrow(() -> new LineStationException("해당 호선에 등록되지 않은 역입니다."));
        return stations.indexOf(lineStation);
    }
}
