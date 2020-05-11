package wooteco.subway.admin.line.service.dto.line;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.admin.line.domain.line.Line;
import wooteco.subway.admin.station.domain.Station;
import wooteco.subway.admin.station.service.dto.StationResponse;

public class LineResponse {

	private Long id;
	private String name;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private String bgColor;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<StationResponse> stations = new LinkedList<>();

	public LineResponse() {
	}

	public LineResponse(Long id, String name, LocalTime startTime, LocalTime endTime,
		int intervalTime, String bgColor, LocalDateTime createdAt, LocalDateTime updatedAt,
		List<StationResponse> stations) {
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.bgColor = bgColor;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.stations = stations;
	}

	public static LineResponse of(Line line) {
		return new LineResponse(line.getId(), line.getName(), line.getStartTime(),
		                        line.getEndTime(), line.getIntervalTime(), line.getBgColor(),
		                        line.getCreatedAt(), line.getUpdatedAt(), new LinkedList<>());
	}

	public static LineResponse of(Line line, List<Station> stations) {
		return new LineResponse(line.getId(), line.getName(), line.getStartTime(),
		                        line.getEndTime(), line.getIntervalTime(), line.getBgColor(),
		                        line.getCreatedAt(), line.getUpdatedAt(),
		                        StationResponse.listOf(stations));
	}

	public static List<LineResponse> listOf(List<Line> lines) {
		return lines.stream()
		            .map(LineResponse::of)
		            .collect(Collectors.toList());
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

	public List<StationResponse> getStations() {
		return stations;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

}
