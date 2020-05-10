package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.annotation.Id;

public class Line {
    private static final long START_STATION = -1L;
    private static final LineStation NOT_EXIST_NEXT_STATION = null;

    @Id
    private Long id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private Set<LineStation> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String bgColor;

    public Line() {
    }

    public Line(Long id, String title, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.bgColor = bgColor;
        stations = new HashSet<>();
    }

    public Line(String title, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this(null, title, startTime, endTime, intervalTime, bgColor);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
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

    public Set<LineStation> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void update(Line line) {
        if (line.getTitle() != null) {
            this.title = line.getTitle();
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

    public List<Long> generateLineStationId() {
        List<Long> lineStationIds = new ArrayList<>();

        LineStation nextStation;
        LineStation startStation = findByPreStationId(START_STATION);
        lineStationIds.add(startStation.getStationId());

        nextStation = getNextStation(startStation);

        while (Objects.nonNull(nextStation)) {
            if (stations.contains(nextStation)) {
                lineStationIds.add(nextStation.getStationId());
            }
            nextStation = getNextStation(nextStation);
        }
        return lineStationIds;
    }

    public void updateLineStation(LineStation updatedLineStation) {
        LineStation existingLineStation = stations.stream()
            .filter(lineStation -> lineStation.equals(updatedLineStation))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("해당 Station 이 없습니다."));

        removeLineStationById(existingLineStation.getStationId());
        addLineStation(updatedLineStation);
    }

    private LineStation getNextStation(LineStation station) {
        LineStation nextStation;
        try {
            nextStation = findByPreStationId(station.getStationId());
        } catch (IllegalArgumentException e) {
            nextStation = NOT_EXIST_NEXT_STATION;
        }
        return nextStation;
    }

    public LineStation findByPreStationId(Long stationId) {
        return stations.stream()
            .filter(lineStation -> lineStation.getPreStationId().equals(stationId))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("해당 preStationId 를 갖는 역을 찾지 못헀습니다."));
    }

    public LineStation findById(Long stationId) {
        return stations.stream()
            .filter(lineStation -> lineStation.getStationId().equals(stationId))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("해당 preStationId 를 갖는 역을 찾지 못헀습니다."));
    }

    public boolean isPresentLineStationGettingPreStationId(Long stationId) {
        return stations.stream()
            .anyMatch(lineStation -> lineStation.getPreStationId().equals(stationId));
    }

    public void removeLineStationById(Long stationId) {
        LineStation targetLineStation = findById(stationId);

        if (isPresentLineStationGettingPreStationId(stationId) && stations.size() != 1) {
            LineStation nextLineStationOfTargetLineStation = findByPreStationId(targetLineStation.getStationId());
            this.stations.remove(nextLineStationOfTargetLineStation);
            nextLineStationOfTargetLineStation.updatePreLineStation(targetLineStation.getPreStationId());
            this.stations.add(nextLineStationOfTargetLineStation);
        }
        this.stations.remove(targetLineStation);
    }

    public void addLineStation(LineStation lineStation) {
        if (isPresentLineStationGettingPreStationId(lineStation.getPreStationId())) {
            LineStation nextLineStationOfInputLineStation = findByPreStationId(lineStation.getPreStationId());
            nextLineStationOfInputLineStation.updatePreLineStation(lineStation.getStationId());
            updateLineStation(nextLineStationOfInputLineStation);
        }
        this.stations.add(lineStation);
    }

    public boolean isStationsEmpty() {
        return stations.isEmpty();
    }

    @Override
    public String toString() {
        return "Line{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", intervalTime=" + intervalTime +
            ", stations=" + stations +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            ", bgColor='" + bgColor + '\'' +
            '}';
    }
}
