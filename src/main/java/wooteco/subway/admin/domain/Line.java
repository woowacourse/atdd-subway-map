package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Line {
    @Id
    private Long id;
    private String name;
    private String bgColor;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private List<LineStation> stations = new LinkedList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Line() {
    }

    public Line(Long id, String name, String bgColor, LocalTime startTime, LocalTime endTime, int intervalTime) {
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
        int addIndex = findAddIndex(lineStation);
        if (addIndex >= stations.size()) {
            stations.add(lineStation);
            return;
        }
        LineStation originLineStation = stations.get(addIndex);
        LineStation modifiedLineStation = new LineStation(lineStation.getStationId(), originLineStation.getStationId());

        stations.remove(originLineStation);
        stations.addAll(addIndex, Arrays.asList(lineStation, modifiedLineStation));
    }

    public int findAddIndex(LineStation addLineStation) {
        if (stations.isEmpty() && !addLineStation.isFirstLineStation()) {
            throw new IllegalArgumentException("첫 역을 먼저 입력해주세요.");
        }

        if (addLineStation.isFirstLineStation()) {
            return 0;
        }

        Long addPreStationId = addLineStation.getPreStationId();

        for (int i = 1; i < stations.size(); i++) {
            Long preStationId = stations.get(i).getPreStationId();
            if (preStationId.equals(addPreStationId)) {
                return i;
            }
        }
        return stations.size();
    }

    public void removeLineStationById(Long stationId) {
        if (stations.isEmpty()) {
            return;
        }
        if (stations.size() == 1) {
            stations.remove(0);
            return;
        }
        // LS size == 1 -> 자기 자신만 있을 때 삭제 예외 처리
        // 첫번째 station (1) 삭제 ->  (null 1)
        if (findFirstLineStation().getStationId().equals(stationId)) {
            LineStation originSecondLineStation = stations.get(1);
            LineStation newFirstLineStation = new LineStation(null, originSecondLineStation.getStationId());
            stations.remove(0);
            stations.remove(0);
            stations.add(0, newFirstLineStation);
            return;
        }
        // 마지막 station 삭제
        if (findLastLineStation().getStationId().equals(stationId)) {
            stations.remove(stations.size() - 1);
            return;
        }
        // 중간 station 삭제
        Map<String, LineStation> betweenLineStations = findBetweenLineStationsById(id);
        int index = findIndexByStationId(id);
        LineStation before = betweenLineStations.get("before");
        LineStation after = betweenLineStations.get("after");
        stations.remove(before);
        stations.remove(after);
        LineStation lineStation = new LineStation(before.getPreStationId(), after.getStationId());
        stations.add(index, lineStation);
    }

    private LineStation findFirstLineStation() {
        return stations.get(0);
    }

    private LineStation findLastLineStation() {
        if (stations.size() == 1) {
            return stations.get(0);
        }
        return stations.get(stations.size() - 1);
    }

    private Map<String, LineStation> findBetweenLineStationsById(Long id) {
        Map<String, LineStation> map = new HashMap<>();
        LineStation before = stations.stream()
                .filter(value -> value.getPreStationId() != null && value.getPreStationId().equals(id))
                .findFirst()
                .get();
        LineStation after = stations.stream()
                .filter(value -> value.getStationId().equals(id))
                .findFirst()
                .get();
        map.put("before", before);
        map.put("after", after);
        return map;
    }

    private int findIndexByStationId(Long id) {
        for (int i = 0; i < stations.size(); i++) {
            if (stations.get(i).getStationId().equals(id)) {
                return i;
            }
        }
        throw new IllegalArgumentException("해당 호선에 존재하지 않는 역입니다.");
    }

    public List<Long> findLineStationsId() {
        List<Long> stationsIds = new ArrayList<>();
        for (LineStation lineStation : stations) {
            stationsIds.add(lineStation.getStationId());
        }
        return stationsIds;
    }
}
