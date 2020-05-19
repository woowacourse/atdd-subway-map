package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Line {
    @Id
    private Long id;
    private String name;
    private String bgColor;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer intervalTime;
    private List<LineStation> stations = new LinkedList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, String bgColor, LocalTime startTime, LocalTime endTime, Integer intervalTime) {
        this.id = id;
        this.name = name;
        this.bgColor = bgColor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
    }

    public Line(String name, String bgColor, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this(null, name, bgColor, startTime, endTime, intervalTime);
    }

    public Line(LocalTime startTime, LocalTime endTime, int intervalTime) {
        this(null, null, null, startTime, endTime, intervalTime);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBgColor() {
        return bgColor;
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

    public void update(Line line) {
        if (line.getName() != null) {
            this.name = line.getName();
        }
        if (line.getBgColor() != null) {
            this.bgColor = line.getBgColor();
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
        if (stations.isEmpty() && !lineStation.isFirstLineStation()) {
            throw new IllegalArgumentException("첫 역을 먼저 입력해주세요.");
        }

        Optional<LineStation> nextLineStation = findNextLineStation(lineStation);
        if (!nextLineStation.isPresent()) {
            stations.add(lineStation);
            return;
        }

        LineStation originLineStation = nextLineStation.get();
        LineStation modifiedLineStation = new LineStation(lineStation.getStationId(), originLineStation.getStationId());

        int addIndex = stations.indexOf(originLineStation);
        stations.remove(originLineStation);
        stations.addAll(addIndex, Arrays.asList(lineStation, modifiedLineStation));
    }

    private Optional<LineStation> findNextLineStation(LineStation lineStation) {
        return stations.stream()
                .filter(lineStation::isNextStation)
                .findAny();
    }

    public void removeLineStationById(Long stationId) {
        Objects.requireNonNull(stationId, "해당 역은 존재하지 않습니다.");
        if (stations.isEmpty()) {
            return;
        }

        LineStation lineStation = findLineStationByStationId(stationId);
        if (isLastStation(lineStation)) {
            stations.remove(lineStation);
            return;
        }

        int removeIndex = stations.indexOf(lineStation);
        LineStation originNextLineStation = stations.get(removeIndex + 1);
        LineStation modifiedStation = new LineStation(lineStation.getPreStationId(), originNextLineStation.getStationId());
        stations.removeAll(Arrays.asList(lineStation, originNextLineStation));
        stations.add(removeIndex, modifiedStation);
    }

    public LineStation findLineStationByStationId(Long stationId) {
        return stations.stream()
                .filter(station -> stationId.equals(station.getStationId()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
    }

    private boolean isLastStation(LineStation lineStation) {
        if (stations.isEmpty()) {
            throw new IllegalArgumentException("경로가 존재하지 않습니다.");
        }
        return stations.get(stations.size() - 1).equals(lineStation);
    }


    public List<Long> findLineStationsId() {
        return stations.stream()
                .mapToLong(LineStation::getStationId)
                .boxed()
                .collect(Collectors.toList());
    }
}
