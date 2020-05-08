package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.annotation.Id;

import wooteco.subway.admin.dto.req.LineRequest;

public class Line {
    private static final int FIRST_INDEX = 0;
    private static final int NEXT_STATION_INDEX = 1;

    @Id
    private Long id;
    private String name;
    private String bgColor;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private List<LineStation> stations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, String bgColor, LocalTime startTime, LocalTime endTime,
        int intervalTime) {
        this.name = name;
        this.bgColor = bgColor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.stations = new ArrayList<>();
    }

    public Line(String name, String bgColor, LocalTime startTime, LocalTime endTime,
        int intervalTime) {
        this(null, name, bgColor, startTime, endTime, intervalTime);
    }

    public static Line of(LineRequest lineRequest) {
        return new Line(
            lineRequest.getName(),
            lineRequest.getBgColor(),
            lineRequest.getStartTime(),
            lineRequest.getEndTime(),
            lineRequest.getIntervalTime());
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

    public List<LineStation> getStations() {
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
        if (lineStation.isFirstOnLine()) {
            update(lineStation, FIRST_INDEX);
            return;
        }

        int targetIndex = IntStream.range(0, stations.size())
            .filter(index -> stations.get(index).isSameWithPreStationId(lineStation))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("이전역이 존재하지 않습니다."));

        update(lineStation, targetIndex + NEXT_STATION_INDEX);
    }

    private void update(LineStation lineStation, int nextStationIndex) {
        stations.add(nextStationIndex, lineStation);

        if (isNotLast(nextStationIndex)) {
            int lastIndex = nextStationIndex + NEXT_STATION_INDEX;
            stations.get(lastIndex).updatePreLineStation(lineStation.getStationId());
        }
    }

    private boolean isNotLast(int nextStation) {
        return stations.size() >= nextStation + 2;
    }

    public void removeLineStationById(Long stationId) {
        int targetIndex = IntStream.range(0, stations.size())
            .filter(index -> stations.get(index).isSameId(stationId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));

        LineStation station = stations.get(targetIndex);
        stations.remove(targetIndex);

        if (targetIndex != stations.size()) {
            stations.get(targetIndex).updatePreLineStation(station.getPreStationId());
        }
    }

    public List<Long> getLineStationsId() {
        return stations.stream()
            .map(LineStation::getStationId)
            .collect(Collectors.toList());
    }
}
