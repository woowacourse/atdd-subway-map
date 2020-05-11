package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Table("LINE")
public class Line {
    @Id
    private Long id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    @MappedCollection(idColumn = "line", keyColumn = "sequence")
    private List<LineStation> lineStations = new ArrayList<>();
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

    public List<LineStation> getLineStations() {
        return lineStations;
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
                ", lineStations=" + lineStations +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", bgColor='" + bgColor + '\'' +
                '}';
    }

    public void update(Line line) {
        // TODO: 검증 로직에 대해서 생각해봐야됨
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

    public void addLineStation(LineStation requestLineStation) {
        int index = lineStations.stream()
                .filter(lineStation -> lineStation.isPreStationOf(requestLineStation))
                .map(lineStation -> lineStations.indexOf(lineStation) + 1)
                .findAny()
                .orElse(0);
        if(lineStations.size() > 1  && lineStations.size() > index) {
            LineStation nextLineStation = lineStations.get(index);
            System.out.println(index + "hello" + lineStations.size());
            if(index == 0) {
                nextLineStation.updatePreLineStationId(requestLineStation.getStationId());
                lineStations.add(index, requestLineStation);
                return;
            }

            LineStation preLineStation = lineStations.get(index - 1);
            requestLineStation.updatePreLineStationId(preLineStation.getStationId());
            nextLineStation.updatePreLineStationId(requestLineStation.getStationId());
            lineStations.add(index, requestLineStation);
            return;
        }
        lineStations.add(index, requestLineStation);
    }

    private boolean isEndIndex(int index) {
        return index >= lineStations.size();
    }

    public void removeLineStationById(Long stationId) {
        int index = lineStations.stream()
                .filter(lineStation -> lineStation.isSameStationId(stationId))
                .map(lineStation -> lineStations.indexOf(lineStation))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
        LineStation preLineStation;
        LineStation nextLineStation;

        if(index == 0 && index == lineStations.size() - 1){
            lineStations.remove(index);
            return;
        }
        if(index == 0) {
            nextLineStation = lineStations.get(1);
            nextLineStation.updatePreLineStationId(null);
            lineStations.remove(index);
            return;
        }
        if(index == lineStations.size() -1)  {
            lineStations.remove(index);
            return;
        }
        nextLineStation = lineStations.get(index+1);
        preLineStation = lineStations.get(index-1);
        nextLineStation.updatePreLineStationId(preLineStation.getStationId());
        lineStations.remove(index);
    }

    public List<Long> getLineStationsId() {
        return this.lineStations.stream()
                .mapToLong(LineStation::getStationId)
                .boxed()
                .collect(Collectors.toList());
    }

}
