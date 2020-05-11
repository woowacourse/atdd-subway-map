package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class Line {
    public static final int START_INDEX = 0;
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String bgColor;
    @MappedCollection
    private List<LineStation> stations = new ArrayList<>();
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
        this.bgColor = bgColor;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime,
        String bgColor) {
        this(null, name, startTime, endTime, intervalTime, bgColor);
    }

    public void addLineStation(LineStation lineStation) {
        if (lineStation.isFirstStation()) {
            addLineStationAtFirst(lineStation);
            return;
        }

        int index = searchLineStationIndex(lineStation.getPreStationId());
        int nextIndex = index + 1;
        if (nextIndex < stations.size()) {
            LineStation target = findLineStationAt(nextIndex);
            target.updatePreLineStation(lineStation.getStationId());
        }

        stations.add(nextIndex, lineStation);
    }

    private void addLineStationAtFirst(LineStation lineStation) {
        stations.add(START_INDEX, lineStation);
        if (stations.size() > 1) {
            findLineStationAt(1).updatePreLineStation(lineStation.getStationId());
        }
    }

    public void removeLineStationById(Long stationId) {
        LineStation lineStation = findLineStationById(stationId);
        int targetIndex = searchLineStationIndex(stationId);

        if (targetIndex != stations.size() - 1) {
            LineStation nextLineStation = findLineStationAt(targetIndex + 1);
            nextLineStation.updatePreLineStation(findLineStationAt(targetIndex).getPreStationId());
        }
        stations.remove(lineStation);
    }

    private LineStation findLineStationById(Long stationId) {
        return stations.stream()
            .filter(lineStation -> lineStation.isSameStation(stationId))
            .findFirst()
            .orElseThrow(RuntimeException::new);
    }

    private int searchLineStationIndex(Long stationId) {
        int index = START_INDEX;
        while (!findLineStationAt(index).isSameStation(stationId)) {
            index++;
        }
        return index;
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

    private LineStation findLineStationAt(int index) {
        return stations.get(index);
    }

    public List<Long> getLineStationsId() {
        return stations.stream()
            .map(LineStation::getStationId)
            .collect(Collectors.toList());
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public String getBgColor() {
        return bgColor;
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
}
