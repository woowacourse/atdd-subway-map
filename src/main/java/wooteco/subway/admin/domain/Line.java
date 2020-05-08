package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class Line {
	@Id
	private Long id;
	private String name;
	private String color;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	@MappedCollection(idColumn = "line", keyColumn = "sequence")
	private List<LineStation> lineStations = new ArrayList<>();
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Line() {
	}

	public Line(Long id, String name, String color, LocalTime startTime, LocalTime endTime, int intervalTime) {
		this.name = name;
		this.color = color;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Line(String name, String color, LocalTime startTime, LocalTime endTime, int intervalTime) {
		this(null, name, color, startTime, endTime, intervalTime);
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

	public String getColor() {
		return color;
	}

	public List<LineStation> getStations() {
		return lineStations;
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
		if (line.getStartTime() != null) {
			this.startTime = line.getStartTime();
		}
		if (line.getEndTime() != null) {
			this.endTime = line.getEndTime();
		}
		if (line.getIntervalTime() != 0) {
			this.intervalTime = line.getIntervalTime();
		}
		if (line.getColor() != null) {
			this.color = line.getColor();
		}

		this.updatedAt = LocalDateTime.now();
	}

	public void addLineStation(LineStation requestLineStation) {
		int index = lineStations.stream()
			.filter(lineStation -> lineStation.isPreStationBy(requestLineStation))
			.map(lineStation -> lineStations.indexOf(lineStation) + 1)
			.findAny()
			.orElse(0);

		lineStations.add(index, requestLineStation);

		int nextLineStationIndex = index + 1;

		if (isExcessIndex(nextLineStationIndex)) {
			return;
		}

		LineStation nextStation = lineStations.get(nextLineStationIndex);
		nextStation.updatePreLineStation(requestLineStation.getStationId());
	}

	private boolean isExcessIndex(int nextLineStationIndex) {
		return nextLineStationIndex == lineStations.size();
	}

	public void removeLineStationById(Long stationId) {
		// TODO: 구현
	}

	public List<Long> getLineStationsId() {
		return lineStations.stream()
			.map(LineStation::getStationId)
			.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "Line{" +
			"id=" + id +
			", name='" + name + '\'' +
			", startTime=" + startTime +
			", endTime=" + endTime +
			", intervalTime=" + intervalTime +
			", stations=" + lineStations +
			", createdAt=" + createdAt +
			", updatedAt=" + updatedAt +
			'}';
	}
}
