package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class Line {
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

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.bgColor = bgColor;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this(null, name, startTime, endTime, intervalTime, bgColor);
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

    public void addLineStation(LineStation lineStation) {
        if (lineStation.getPreStationId() == null) {
            addLineStationAtFirst(lineStation);
        } else {
            for (int index = 0; index < stations.size(); index++) {
                LineStation currentLineStation = stations.get(index);
                int nextIndex = index + 1;
                if (currentLineStation.getStationId().equals(lineStation.getPreStationId())
                        && nextIndex < stations.size()) {
                    LineStation target = stations.get(nextIndex);
                    target.updatePreLineStation(lineStation.getStationId());
                    stations.add(nextIndex, lineStation);
                    break;
                }
                if (currentLineStation.getStationId().equals(lineStation.getPreStationId())
                        && nextIndex == stations.size()) {
                    stations.add(lineStation);
                    break;
                }
            }
        }
    }

    private void addLineStationAtFirst(LineStation lineStation) {
        stations.add(0, lineStation);
        if (stations.size() > 1) {
            stations.get(1).updatePreLineStation(lineStation.getStationId());
        }
    }

    public void removeLineStationById(Long stationId) {
        LineStation lineStation = stations.stream()
                .filter(station -> station.getStationId().equals(stationId))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        int targetIndex = findLineStationIndex(stationId);
        if (targetIndex != stations.size() - 1) {
            LineStation nextLineStation = stations.get(targetIndex + 1);
            nextLineStation.updatePreLineStation(stations.get(targetIndex).getPreStationId());
        }
        stations.remove(lineStation);
    }

    private int findLineStationIndex(Long stationId) {
        for (int index = 0; index < stations.size(); index++) {
            LineStation currentLineStation = stations.get(index);
            if (currentLineStation.getStationId().equals(stationId)) {
                return index;
            }
        }
        return 0;
    }

    public List<Long> getLineStationsId() {
        return stations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
    }
}
