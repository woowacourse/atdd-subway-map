package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import wooteco.subway.admin.exception.InvalidLineStationException;
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
        int stationsSize = stations.size();
        int insertIndex = IntStream.range(0, stationsSize)
            .filter(index -> stations.get(index).isPreStation(lineStation.getPreStationId()))
            .findAny()
            .orElse(stationsSize);
        stations.add(insertIndex, lineStation);
        updatePreLineStation(insertIndex, lineStation.getStationId());
    }

    private void validateHavingSame(LineStation lineStation) {
        boolean isExistLineStation = stations.stream()
            .anyMatch(station -> station.isSameStation(lineStation));
        if (isExistLineStation) {
            throw new InvalidLineStationException("이미 등록된 구간입니다");
        }
    }

    public void updatePreLineStation(int index, Long stationId) {
        if (stations.size() - 1 == index) {
            return;
        }
        LineStation lineStation = stations.get(index + 1);
        lineStation.updatePreLineStation(stationId);
    }

    public void removeLineStationById(Long stationId) {
        int removeIndex = IntStream.range(0, stations.size())
            .filter(index -> stations.get(index).isBaseStation(stationId))
            .findAny()
            .orElseThrow(() -> new InvalidLineStationException("id를 찾을 수 없습니다."));

        if (stations.size() - 1 != removeIndex) {
            LineStation lineStation = stations.get(removeIndex);
            stations.get(removeIndex + 1).updatePreLineStation(lineStation.getPreStationId());
        }
        stations.remove(removeIndex);
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
