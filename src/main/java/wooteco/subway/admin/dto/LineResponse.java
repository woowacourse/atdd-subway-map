package wooteco.subway.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import wooteco.subway.admin.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private String startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private String endTime;
    private int intervalTime;
    private String createdAt;
    private String updatedAt;
    private String bgColor;
    private String[] stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String startTime, String endTime, int intervalTime, String createdAt, String updatedAt, String[] stations, String bgColor) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.stations = stations;
        this.bgColor = bgColor;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getStartTime().toString(), line.getEndTime().toString(), line.getIntervalTime(), line.getCreatedAt().toString(), line.getUpdatedAt().toString(), new String[0], line.getBgColor());
    }

    public static List<LineResponse> listOf(List<Line> lines) {
        return lines.stream()
                .map(it -> LineResponse.of(it))
                .collect(Collectors.toList());
    }

    public static LineResponse of(Line line, List<StationResponse> stations) {
        String[] stationArray = new String[stations.size()];
        int i = 0;
        for (StationResponse stationResponse : stations) {
            stationArray[i++] = stationResponse.getName();
        }
        return new LineResponse(line.getId(), line.getName(), line.getStartTime().toString(), line.getEndTime().toString(), line.getIntervalTime(), line.getCreatedAt().toString(), line.getUpdatedAt().toString(), stationArray, line.getBgColor());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public String[] getStations() {
        return stations;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getBgColor() {
        return bgColor;
    }
}
