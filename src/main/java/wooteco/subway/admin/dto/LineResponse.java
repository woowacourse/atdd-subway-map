package wooteco.subway.admin.dto;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

public class LineResponse {

    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String bgColor;

    private List<Station> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name,
        LocalTime startTime, LocalTime endTime,
        int intervalTime, String bgColor,
        List<Station> stations) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.bgColor = bgColor;
        this.stations = stations;
    }

    public static LineResponse of(Line line, List<Station> stations) {
        return new LineResponse(
            line.getId(), line.getName(),
            line.getLineTimeTable().getStartTime(),
            line.getLineTimeTable().getEndTime(),
            line.getLineTimeTable().getIntervalTime(),
            line.getBgColor(), stations);
    }

    public static LineResponse of(Line line) {
        return of(line, new ArrayList<>());
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

    public List<Station> getStations() {
        return stations;
    }
}
