package wooteco.subway.admin.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

public class LineResponse {
	private Long id;
	private String title;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private String bgColor;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private Set<StationResponse> stations;

	private LineResponse() {
	}

	private LineResponse(Long id, String title, LocalTime startTime, LocalTime endTime,
		int intervalTime, String bgColor, LocalDateTime createdAt,
		LocalDateTime updatedAt, Set<StationResponse> stations) {
		this.id = id;
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.bgColor = bgColor;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.stations = stations;
	}

	public static LineResponse of(Line line, Set<Station> stations) {
		Set<StationResponse> stationResponses = stations.stream()
			.map(StationResponse::of)
			.collect(Collectors.toSet());

		return new LineResponse(line.getId(), line.getName(), line.getStartTime(),
			line.getEndTime(), line.getIntervalTime(), line.getBgColor(),
			line.getCreatedAt(), line.getUpdatedAt(), stationResponses);
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
