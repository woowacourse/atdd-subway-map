package wooteco.subway.admin.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

public class LineResponse {

	private Long id;
	private String name;
	private String color;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private List<Station> stations;

	private LineResponse() {
	}

	public LineResponse(Long id, String name, String color, LocalTime startTime, LocalTime endTime,
		int intervalTime, LocalDateTime createdAt, LocalDateTime updatedAt,
		List<Station> stations) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.stations = new ArrayList<>(stations);
	}

	public static LineResponse of(Line line) {
		return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getStartTime(),
			line.getEndTime(), line.getIntervalTime(), line.getCreatedAt(), line.getUpdatedAt(),
			new ArrayList<>());
	}

	public static List<LineResponse> listOf(List<Line> lines) {
		return lines.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public static LineResponse of(Line line, List<Station> station) {
		return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getStartTime(),
			line.getEndTime(), line.getIntervalTime(), line.getCreatedAt(), line.getUpdatedAt(),
			station);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
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

	public List<Station> getStations() {
		return stations;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		LineResponse that = (LineResponse)o;
		return intervalTime == that.intervalTime &&
			Objects.equals(id, that.id) &&
			Objects.equals(name, that.name) &&
			Objects.equals(color, that.color) &&
			Objects.equals(startTime, that.startTime) &&
			Objects.equals(endTime, that.endTime) &&
			Objects.equals(createdAt, that.createdAt) &&
			Objects.equals(updatedAt, that.updatedAt) &&
			Objects.equals(stations, that.stations);
	}

	@Override
	public int hashCode() {
		return Objects
			.hash(id, name, color, startTime, endTime, intervalTime, createdAt, updatedAt,
				stations);
	}

	@Override
	public String toString() {
		return "LineResponse{" +
			"id=" + id +
			", name='" + name + '\'' +
			", color='" + color + '\'' +
			", startTime=" + startTime +
			", endTime=" + endTime +
			", intervalTime=" + intervalTime +
			", createdAt=" + createdAt +
			", updatedAt=" + updatedAt +
			", stations=" + stations +
			'}';
	}
}
