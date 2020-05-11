package wooteco.subway.admin.dto;

import com.google.common.collect.Sets;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

public class LineResponse {
    private Long id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String bgColor;

    private Set<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String title, LocalTime startTime, LocalTime endTime, int intervalTime,
        LocalDateTime createdAt, LocalDateTime updatedAt, String bgColor,
        Set<StationResponse> stations) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.bgColor = bgColor;
        this.stations = Sets.newLinkedHashSet(stations);
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getTitle(), line.getStartTime(), line.getEndTime(),
            line.getIntervalTime(), line.getCreatedAt(), line.getUpdatedAt(), line.getBgColor(), new LinkedHashSet<>());
    }

    public static LineResponse of(final Line line, final Set<StationResponse> stations) {
        return new LineResponse(line.getId(), line.getTitle(), line.getStartTime(), line.getEndTime(),
                line.getIntervalTime(), line.getCreatedAt(), line.getUpdatedAt(), line.getBgColor(), stations);
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

    public Set<StationResponse> getStations() {
        return Sets.newLinkedHashSet(stations);
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
}
