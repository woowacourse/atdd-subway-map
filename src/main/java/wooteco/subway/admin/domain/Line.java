package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import wooteco.subway.admin.utils.Validator;

public class Line {
    @Id
    private Long id;
    private String name;
    private String color;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    @MappedCollection(idColumn = "line_id", keyColumn = "index")
    private List<LineStation> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, String color, LocalTime startTime, LocalTime endTime,
        int intervalTime) {
        Validator.validateNotEmpty(name);
        Validator.validateNotContainsBlank(name);
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
        validateHavingSame(lineStation);
        int insertIndex = findInsertIndex(lineStation.getPreStationId());
        stations.add(insertIndex, lineStation);
        updatePreLineStation(insertIndex, lineStation.getStationId());
    }

    private void validateHavingSame(LineStation lineStation) {
        boolean isExistLineStation = stations.stream()
            .anyMatch(station -> station.isSameStation(lineStation));
        if (isExistLineStation) {
            throw new IllegalArgumentException("이미 등록된 구간입니다");
        }
    }

    private int findInsertIndex(Long stationId) {
        int stationsSize = stations.size();
        return IntStream.range(0, stationsSize)
            .filter(index -> stations.get(index).isPreStation(stationId))
            .findAny()
            .orElse(stationsSize);
    }

    public void updatePreLineStation(int index, Long stationId) {
        if (stations.size() == index - 1) {
            return;
        }
        LineStation lineStation = stations.get(index + 1);
        lineStation.updatePreLineStation(stationId);
    }

    public void removeLineStationById(Long stationId) {
        int index = findInsertIndex(stationId);
        if (stations.size() != index + 1) {
            LineStation lineStation = stations.get(index);
            stations.get(index + 1).updatePreLineStation(lineStation.getPreStationId());
        }
        stations.remove(index);
    }

    public List<Long> makeLineStationsIds() {
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
}
