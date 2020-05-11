package wooteco.subway.admin.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

public class LineResponse {
	private Long id;
	private String name;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String bgColor;

	private List<StationResponse> stations;

	private LineResponse() {
	}

	public LineResponse(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime,
		LocalDateTime createdAt, LocalDateTime updatedAt, String bgColor, List<StationResponse> stations) {
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.bgColor = bgColor;
		this.stations = stations;
	}

	public static LineResponse of(Line line) {
		return of(line, new LinkedList<>());
	}

	public static LineResponse of(Line line, List<Station> stations) {
		return new LineResponse(line.getId(), line.getName(), line.getStartTime(), line.getEndTime(),
			line.getIntervalTime(), line.getCreatedAt(), line.getUpdatedAt(), line.getBgColor(),
			StationResponse.ofList(stations));
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

	public List<StationResponse> getStations() {
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
}
