package wooteco.subway.admin.dto;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
	private Set<Station> stations;

	public LineResponse() {
	}

	public LineResponse(Long id, String name, String color, LocalTime startTime,
			LocalTime endTime, int intervalTime, Set<Station> stations) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.stations = stations;
	}

	public static LineResponse of(Line line) {
		return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getStartTime(),
				line.getEndTime(), line.getIntervalTime(), new HashSet<>());
	}

	public static LineResponse of(Line line, Set<Station> stations) {
		return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getStartTime(),
				line.getEndTime(), line.getIntervalTime(), stations);
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

	public Set<Station> getStations() {
		return stations;
	}
}
