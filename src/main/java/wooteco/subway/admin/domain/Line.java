package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class Line {
    private static final int FIRST_STATION_INDEX = 0;
    private static final int SECOND_INDEX = 1;
    private static final int ONLY_ONE_STATION = 1;

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
        if (lineStation.isStartStation()) {
            addStartLineStation(lineStation);
            return;
        }
        validateLineStation(lineStation);
        addBetweenLineStation(lineStation);
    }

    private void validateHavingSame(LineStation lineStation) {
        boolean isExistLineStation = stations.stream()
            .anyMatch(station -> station.isSameStation(lineStation));
        if (isExistLineStation) {
            throw new IllegalArgumentException("이미 등록된 구간입니다");
        }
    }

    private void addStartLineStation(LineStation lineStation) {
        stations.add(FIRST_STATION_INDEX, lineStation);
        if (stations.size() != ONLY_ONE_STATION) {
            LineStation secondLineStation = stations.get(SECOND_INDEX);
            secondLineStation.updatePreLineStation(lineStation.getStationId());
        }
    }

    private void validateLineStation(LineStation lineStation) {
        if (lineStation.isSameBothStation()) {
            throw new IllegalArgumentException("같은 역을 출발지점과 도착지점으로 정할 수 없습니다.");
        }
    }

    private void addBetweenLineStation(LineStation lineStation) {
        int insertIndex = findPreStationIndex(lineStation.getPreStationId()) + 1;
        if (stations.size() != insertIndex) {
            LineStation existing = stations.get(insertIndex);
            existing.updatePreLineStation(lineStation.getStationId());
        }
        stations.add(insertIndex, lineStation);
    }

    public void removeLineStationById(Long stationId) {
        int index = findPreStationIndex(stationId);
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

    private int findPreStationIndex(Long stationId) {
        return IntStream.range(0, stations.size())
            .filter(index -> stations.get(index).isBaseStation(stationId))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이전역입니다."));
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
