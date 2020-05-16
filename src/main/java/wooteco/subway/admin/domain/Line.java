package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class Line {
    private static final String DEFAULT_BACKGROUND_COLOR = "bg-blue-500";
    private static final String ERROR_MESSAGE_NO_CONTACT_STATION = "연결할 수 있는 역이 없습니다.";

    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    @Column("bg_color")
    private String backgroundColor;
    private List<LineStation> lineStations = new LinkedList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.backgroundColor = DEFAULT_BACKGROUND_COLOR;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this(null, name, startTime, endTime, intervalTime);
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String backgroundColor) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.backgroundColor = backgroundColor;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

        this.updatedAt = LocalDateTime.now();
    }

    public void addLineStation(LineStation lineStation) {
        if (lineStations.isEmpty()) {
            lineStations.add(lineStation);
            return;
        }

        if (lineStation.isFirstLineStation()) {
            LineStation firstLineStation = lineStations.get(0);
            firstLineStation.modifyPreStationId(lineStation.getStationId());
            lineStations.add(0, lineStation);
            return;
        }

        LineStation targetLineStation = getLineStationBy(lineStation.getPreStationId());
        int nextLineStationIndex = getNextLineStationIndex(targetLineStation);

        if (isInclude(nextLineStationIndex)) {
            LineStation nextLineStation = lineStations.get(nextLineStationIndex);
            nextLineStation.modifyPreStationId(lineStation.getStationId());
            lineStations.add(nextLineStationIndex, lineStation);
            lineStations.set(nextLineStationIndex + 1, nextLineStation);
            return;
        }

        lineStations.add(lineStation);
    }

    private int getNextLineStationIndex(LineStation targetLineStation) {
        return lineStations.indexOf(targetLineStation) + 1;
    }

    private boolean isInclude(int nextLineStationIndex) {
        return nextLineStationIndex <= lineStations.size() - 1;
    }

    private LineStation getLineStationBy(Long id) {
        return lineStations.stream()
                .filter(station -> station.getStationId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NO_CONTACT_STATION));
    }

    public void removeLineStationById(Long stationId) {
        LineStation targetLineStation = getLineStationBy(stationId);
        int nextLineStationIndex = getNextLineStationIndex(targetLineStation);

        if (targetLineStation.isFirstLineStation()) {
            LineStation nextLineStation = lineStations.get(nextLineStationIndex);
            nextLineStation.modifyPreStationId(nextLineStation.getStationId());
            lineStations.set(nextLineStationIndex, nextLineStation);
            lineStations.remove(targetLineStation);
            return;
        }

        if (isInclude(nextLineStationIndex)) {
            LineStation nextLineStation = lineStations.get(nextLineStationIndex);
            LineStation preLineStation = lineStations.get(nextLineStationIndex - 1);
            nextLineStation.modifyPreStationId(preLineStation.getStationId());
            lineStations.set(nextLineStationIndex, nextLineStation);
            lineStations.remove(targetLineStation);
            return;
        }

        lineStations.remove(targetLineStation);
    }

    public List<Long> findLineStationsId() {
        return lineStations.stream()
                .map(LineStation::getStationId)
                .collect(collectingAndThen(toList(), Collections::unmodifiableList));
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

    public List<LineStation> getLineStations() {
        return lineStations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }
}
