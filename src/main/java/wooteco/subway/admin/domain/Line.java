package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import wooteco.subway.admin.exception.DuplicateLineStationException;

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
        this.id = id;
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
        validateDuplicateLineStation(lineStation);
        if (Objects.isNull(lineStation.getPreStationId())) {
            addLineStationAtFirst(lineStation);
        } else {
            addLineStationAfterFirst(lineStation);
        }
    }

    private void validateDuplicateLineStation(LineStation lineStation) {
        if (hasDuplicateLineStation(lineStation)) {
            throw new DuplicateLineStationException();
        }
    }

    private boolean hasDuplicateLineStation(LineStation lineStation) {
        return stations.stream()
                .anyMatch(station -> station.isSameStation(lineStation));
    }

    private void addLineStationAfterFirst(LineStation lineStation) {
        for (int index = 0; index < stations.size(); index++) {
            LineStation currentLineStation = stations.get(index);
            int nextIndex = index + 1;
            if (containsPreStationInBetween(lineStation, currentLineStation, nextIndex)) {
                addLineInBetween(lineStation, nextIndex);
                break;
            }
            if (containsPreStationAtLast(lineStation, currentLineStation, nextIndex)) {
                stations.add(lineStation);
                break;
            }
        }
    }

    private boolean containsPreStationInBetween(LineStation lineStation, LineStation currentLineStation,
            int nextIndex) {
        return currentLineStation.getStationId().equals(lineStation.getPreStationId()) && nextIndex < stations.size();
    }

    private boolean containsPreStationAtLast(LineStation lineStation, LineStation currentLineStation, int nextIndex) {
        return currentLineStation.getStationId().equals(lineStation.getPreStationId())
                && nextIndex == stations.size();
    }

    private void addLineInBetween(LineStation lineStation, int nextIndex) {
        LineStation target = stations.get(nextIndex);
        target.updatePreLineStation(lineStation.getStationId());
        stations.add(nextIndex, lineStation);
    }

    private void addLineStationAtFirst(LineStation lineStation) {
        stations.add(0, lineStation);
        if (stations.size() > 1) {
            stations.get(1).updatePreLineStation(lineStation.getStationId());
        }
    }

    public void removeLineStationById(Long stationId) {
        LineStation lineStation = findLineStationByStationId(stationId);
        int targetIndex = findLineStationIndex(stationId);
        if (targetIndex != stations.size() - 1) {
            LineStation nextLineStation = stations.get(targetIndex + 1);
            nextLineStation.updatePreLineStation(stations.get(targetIndex).getPreStationId());
        }
        stations.remove(lineStation);
    }

    private LineStation findLineStationByStationId(Long stationId) {
        return stations.stream()
                .filter(station -> station.getStationId().equals(stationId))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private int findLineStationIndex(Long stationId) {
        return IntStream.range(0, stations.size())
                .filter(index -> stations.get(index).isSameStationId(stationId))
                .findAny()
                .orElse(0);
    }

    public List<Long> getLineStationsId() {
        return stations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
    }
}
