package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.annotation.Id;

public class Line {
    public static final long START_STATION = -1L;

    @Id
    private Long id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String bgColor;
    private Set<LineStation> stations;

    private Line() {
    }

    public Line(String title, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.bgColor = bgColor;
        stations = new HashSet<>();
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

    private Optional<LineStation> findByPreStationId(Long preStationId) {
        return stations.stream()
            .filter(lineStation -> lineStation.getPreStationId().equals(preStationId))
            .findFirst();
    }

    private Optional<LineStation> findById(Long stationId) {
        return stations.stream()
            .filter(lineStation -> lineStation.getStationId().equals(stationId))
            .findFirst();
    }

    public void removeLineStationById(Long stationId) {
        LineStation LineStation = findById(stationId).orElseThrow(
            () -> new IllegalArgumentException("targetLineStation을 찾을 수 없습니다."));

        if (findByPreStationId(stationId).isPresent()) {
            LineStation nextLineStation = findByPreStationId(
                LineStation.getStationId()).orElseThrow(
                () -> new IllegalArgumentException("해당역의 다음역을 찾을 수 없어 LineStation을 remove할 수 없습니다."));

            nextLineStation.updatePreLineStation(LineStation.getPreStationId());
        }
        this.stations.remove(LineStation);
    }

    public void addLineStation(LineStation lineStation) {
        if (findByPreStationId(lineStation.getPreStationId()).isPresent()) {
            LineStation nextLineStation = findByPreStationId(
                lineStation.getStationId()).orElseThrow(
                () -> new IllegalArgumentException("nextStation을 찾을 수 없어 LineStation을 add 할 수 없습니다."));

            nextLineStation.updatePreLineStation(lineStation.getStationId());
        }
        this.stations.add(lineStation);
    }

    public boolean isStationsEmpty() {
        return stations.isEmpty();
    }

    public List<Long> getLineStationIds() {
        LineStation firstLineStation = findByPreStationId(START_STATION).orElseThrow(
            () -> new IllegalArgumentException("시작역을 찾을 수 없습니다."));

        List<Long> stationIds = new ArrayList<>();
        stationIds.add(firstLineStation.getStationId());

        Long lastStationId = stationIds.get(stationIds.size() - 1);
        Optional<LineStation> nextLineStation = findById(lastStationId);

        while (nextLineStation.isPresent()) {
            stationIds.add(nextLineStation.get().getStationId());

            lastStationId = stationIds.get(stationIds.size() - 1);
            nextLineStation = findById(lastStationId);
        }
        return stationIds;
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
