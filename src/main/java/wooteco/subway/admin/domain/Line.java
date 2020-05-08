package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Line {
	@Id
	private Long id;
	private String title;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private String bgColor;
	private Set<LineStation> stations = new LinkedHashSet<>();
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Line() {
	}

	public Line(Long id, String title, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
		this.id = id;
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.bgColor = bgColor;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Line(Long id, String title, LocalTime startTime, LocalTime endTime, int intervalTime) {
		this(id, title, startTime, endTime, intervalTime, "bgColor");
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

	public String getBgColor() {
		return bgColor;
	}

	public Set<LineStation> getStations() {
		return stations;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void update(Line line) {
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

	public void addLineStation(LineStation lineStation) {
		if (stations == null) {
			stations = new HashSet<>();
		}
		stations.add(lineStation);
	}

	public void removeLineStationById(Long stationId) {
		LineStation lineStation = stations.stream()
			.filter(value -> value.getStationId().equals(stationId))
			.findFirst()
			.orElseThrow(NoSuchElementException::new);

		stations.remove(lineStation);
	}

	public List<Long> getLineStationsId() {
		return stations.stream()
			.map(LineStation::getStationId)
			.collect(Collectors.toList());
	}
}
