package wooteco.subway.admin.domain.line;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;

public class Line {
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String bgColor;
    private Set<LineStation> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime,
        String bgColor, Set<LineStation> stations) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.bgColor = bgColor;
        this.stations = stations;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
        if (stations.isEmpty() && lineStation.isFirstNode()) {
            stations.add(lineStation);
            return;
        }
        if (lineStation.isFirstNode()) {
            stations.stream()
                .filter(LineStation::isFirstNode)
                .findFirst()
                .orElseThrow(AssertionError::new)
                .updatePreLineStation(lineStation.getStationId());
        } else {
            LineStation preNodeOfInput = stations.stream()
                .filter(station -> station.isPreNodeOf(lineStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "ID = " + lineStation.getPreStationId() + "인 역이 존재하지 않습니다.")
                );

            stations.stream()
                .filter(preNodeOfInput::isPreNodeOf)
                .findFirst()
                .ifPresent(station -> station.updatePreLineStation(lineStation.getStationId()));
        }

        stations.add(lineStation);
    }

    public void removeLineStationById(Long stationId) {
        LineStation nodeToRemove = stations.stream()
            .filter(lineStation -> lineStation.sameStationId(stationId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "ID = " + stationId + "인 역이 존재하지 않습니다.")
            );

        stations.stream()
            .filter(nodeToRemove::isPreNodeOf)
            .findFirst()
            .ifPresent(lineStation -> lineStation.updatePreLineStation(nodeToRemove.getPreStationId()));

        stations.remove(nodeToRemove);
    }

    public List<Long> getSortedStationsId() {
        List<Long> sortedStationsId = new ArrayList<>();
        if (stations.isEmpty()) {
            return sortedStationsId;
        }

        LineStation preNode = stations.stream()
            .filter(LineStation::isFirstNode)
            .findFirst()
            .orElseThrow(AssertionError::new);
        sortedStationsId.add(preNode.getStationId());

        while (sortedStationsId.size() < stations.size()) {
            LineStation currentNode = stations.stream()
                .filter(preNode::isPreNodeOf)
                .findFirst()
                .orElseThrow(AssertionError::new);
            sortedStationsId.add(currentNode.getStationId());
            preNode = currentNode;
        }

        return sortedStationsId;
    }
}
