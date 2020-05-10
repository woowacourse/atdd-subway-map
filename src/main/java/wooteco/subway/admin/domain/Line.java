package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;

public class Line {
    private static final int DEFAULT_INDEX = -1;

    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String backgroundColor;
    private List<LineStation> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String backgroundColor) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.backgroundColor = backgroundColor;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.stations = new ArrayList<>();
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String backgroundColor) {
        this(null, name, startTime, endTime, intervalTime, backgroundColor);
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

    public String getBackgroundColor() {
        return backgroundColor;
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
        if (line.getBackgroundColor() != null) {
            this.backgroundColor = line.getBackgroundColor();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation lineStation) {
        if (stations.size() != 0) {
            int index = findStationsIndex(lineStation);
            addAtIndexDefault(lineStation, index);
            addAtIndexNotDefault(lineStation, index);
        }
        if (stations.size() == 0) {
            executePreStationIdNotNull(lineStation);
            stations.add(lineStation);
        }
    }


    private int findStationsIndex(LineStation lineStation) {
        int index = DEFAULT_INDEX;
        for (int i = 0; i < stations.size(); i++) {
            index = getIndex(lineStation, index, i);
        }
        return index;
    }

    private int getIndex(LineStation lineStation, int index, int i) {
        if (stations.get(i).getPreStationId() == lineStation.getPreStationId()) {
            stations.get(i).updatePreLineStation(lineStation.getStationId());
            index = i;
        }
        return index;
    }

    private void addAtIndexDefault(LineStation lineStation, int index) {
        if (index == DEFAULT_INDEX) {
            stations.add(lineStation);
        }
    }

    private void addAtIndexNotDefault(LineStation lineStation, int index) {
        if (index != DEFAULT_INDEX) {
            stations.add(index, lineStation);
        }
    }

    private void executePreStationIdNotNull(LineStation lineStation) {
        if (lineStation.getPreStationId() != null) {
            stations.add(new LineStation(null, lineStation.getPreStationId(), 0, 0));
        }
    }

    public void removeLineStationById(Long stationId) {
        LineStation lineStation = stations.stream()
                .filter(station -> station.getStationId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 station 정보가 없습니다."));

        stations.remove(lineStation);

        updateAfterRemove();
    }

    private void updateAfterRemove() {
        for (int i = 0; i < stations.size() - 1; i++){
            beFirstIndex(i);
            beNotFirstIndex(i);
        }
    }

    private void beFirstIndex(int index) {
        if (index == 0) {
            updateFirstIndex(index);
        }
    }

    private void updateFirstIndex(int index) {
        if (stations.get(index).getPreStationId() != null) {
            stations.get(index).updatePreLineStation(null);
        }
    }

    private void beNotFirstIndex(int index) {
        if (index != 0) {
            updateNotFirstIndex(index);
        }
    }

    private void updateNotFirstIndex(int index) {
        if (stations.get(index).getStationId() != stations.get(index+1).getPreStationId()) {
            stations.get(index+1).updatePreLineStation(stations.get(index).getStationId());
        }
    }

    public List<Long> getLineStationsId() {
        return stations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
    }
}
