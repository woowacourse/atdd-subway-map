package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Embedded;

import wooteco.subway.admin.domain.vo.LineSchedule;
import wooteco.subway.admin.domain.vo.LineStations;
import wooteco.subway.admin.exception.LineStationException;

public class Line {
    @Id
    private Long id;
    private String name;
    private String color;
    @Embedded.Nullable
    private LineSchedule lineSchedule;
    @Embedded.Empty
    private LineStations lineStations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PersistenceConstructor
    public Line(Long id, String name, String color, LineSchedule lineSchedule,
        LineStations lineStations,
        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.lineSchedule = lineSchedule;
        this.lineStations = lineStations;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Line of(Long id, String name, String color, LocalTime startTime,
        LocalTime endTime,
        int intervalTime) {
        LineSchedule lineSchedule = LineSchedule.of(startTime, endTime, intervalTime);
        return new Line(id, name, color, lineSchedule, LineStations.empty(), LocalDateTime.now(),
            LocalDateTime.now());
    }

    public static Line withoutId(String name, String color, LocalTime startTime, LocalTime endTime,
        int intervalTime) {
        LineSchedule lineSchedule = LineSchedule.of(startTime, endTime, intervalTime);
        return new Line(null, name, color, lineSchedule, LineStations.empty(), LocalDateTime.now(),
            LocalDateTime.now());
    }

    public void update(Line line) {
        if (line.getName() != null) {
            this.name = line.getName();
        }
        if (line.getColor() != null) {
            this.color = line.getColor();
        }
        if (line.lineSchedule != null) {
            this.lineSchedule = line.lineSchedule;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation lineStation) {
        validateLineStation(lineStation);
        int insertIndex = findInsertIndex(lineStation.getPreStationId());
        Long stationId = lineStation.getStationId();
        updatePreStation(insertIndex, stationId);
        lineStations.add(insertIndex, lineStation);
    }

    public void removeStationBy(Long stationId) {
        int index = findStationIndex(stationId);
        int indexToUpdate = index + 1;
        Long preStationdId = lineStations.get(index).getPreStationId();
        updatePreStation(indexToUpdate, preStationdId);
        lineStations.remove(index);
    }

    public List<Long> getStationsId() {
        LinkedList<Long> stations = new LinkedList<>();
        for (LineStation lineStation : this.lineStations.getStations()) {
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
        return lineSchedule.getStartTime();
    }

    public LocalTime getEndTime() {
        return lineSchedule.getEndTime();
    }

    public int getIntervalTime() {
        return lineSchedule.getIntervalTime();
    }

    public List<LineStation> getLineStations() {
        return lineStations.getStations();
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
        if (!lineStation.isFirstLineStation() && lineStations.isEmpty()) {
            throw new LineStationException("첫 노선을 먼저 등록해야 합니다.");
        }
    }

    private void validateStations(LineStation lineStation) {
        if (lineStation.getStationId().equals(lineStation.getPreStationId())) {
            throw new LineStationException("같은 역을 출발지점과 도착지점으로 정할 수 없습니다.");
        }
    }

    private void validateHavingSame(LineStation lineStation) {
        for (LineStation station : lineStations.getStations()) {
            if (station.hasSameStations(lineStation)) {
                throw new LineStationException("이미 등록된 구간입니다.");
            }
        }
    }

    private void updatePreStation(int index, Long stationId) {
        if (lineStations.size() != index) {
            LineStation existing = lineStations.get(index);
            existing.updatePreLineStation(stationId);
        }
    }

    private int findInsertIndex(Long preStationId) {
        if (Objects.isNull(preStationId)) {
            return 0;
        }
        LineStation last = lineStations.getLast();
        if (preStationId.equals(last.getStationId())) {
            return lineStations.size();
        }
        LineStation preStation = lineStations.getStations().stream()
            .filter(station -> station.samePreStation(preStationId))
            .findAny()
            .orElseThrow(() -> new LineStationException("현재 노선에 등록되지 않은 이전역입니다."));
        return lineStations.indexOf(preStation);
    }

    private int findStationIndex(Long stationId) {
        LineStation lineStation = lineStations.getStations().stream()
            .filter(station -> station.isBaseStation(stationId))
            .findFirst()
            .orElseThrow(() -> new LineStationException("해당 호선에 등록되지 않은 역입니다."));
        return lineStations.indexOf(lineStation);
    }
}
