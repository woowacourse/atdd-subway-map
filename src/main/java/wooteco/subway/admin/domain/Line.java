package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import org.springframework.data.annotation.Id;

public class Line {
	@Id
	private Long id;
	private String color;
	private String name;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private Set<LineStation> stations;
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
		this.stations = new HashSet<>();
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Line(String name, String color, LocalTime startTime, LocalTime endTime, int intervalTime) {
		this(null, name, color, startTime, endTime, intervalTime);
	}

	public void update(Line line) {
		if (line.getName() != null) {
			this.name = line.getName();
		}
		if (line.getColor() != null) {
			this.color = line.getColor();
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

		this.updatedAt = LocalDateTime.now();
	}

	public void addLineStation(LineStation lineStation) {
		if (lineStation.isStartStation() && stations.size() != 0) {
			LineStation startLineStation = stations.stream()
				.filter(LineStation::isStartStation)
				.findFirst()
				.orElseThrow(RuntimeException::new);
			startLineStation.updatePreLineStation(lineStation.getStationId());
		}
		stations.stream()
			.filter(station -> Objects.nonNull(lineStation.getPreStationId()) && lineStation.getPreStationId()
				.equals(station.getPreStationId()))
			.findFirst()
			.ifPresent(station -> station.updatePreLineStation(lineStation.getStationId()));
		stations.add(lineStation);
	}

	public void removeLineStationById(Long stationId) {
		LineStation lineStation = stations.stream()
			.filter(station -> station.getStationId().equals(stationId))
			.findFirst()
			.orElseThrow(RuntimeException::new);
		stations.stream()
			.filter(
				station -> Objects.nonNull(station.getPreStationId()) && station.getPreStationId().equals(stationId))
			.findFirst()
			.ifPresent(nextLineStation -> nextLineStation.updatePreLineStation(lineStation.getPreStationId()));
		stations.remove(lineStation);
	}

	public List<Long> getLineStationsId() {
		List<Long> lineStationsId = new ArrayList<>();
		stations.stream()
			.filter(station -> station.getPreStationId() == null)
			.findFirst().ifPresent(startLineStation -> {

			Queue<Long> queue = new LinkedList<>();
			queue.add(startLineStation.getStationId());

			while (!queue.isEmpty()) {
				Long stationId = queue.poll();
				lineStationsId.add(stationId);
				stations.stream()
					.filter(station -> stationId.equals(station.getPreStationId()))
					.findFirst()
					.ifPresent(lineStation -> queue.add(lineStation.getStationId()));
			}
		});
		return lineStationsId;
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

	public Set<LineStation> getStations() {
		return stations;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setColor(final String color) {
		this.color = color;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setStartTime(final LocalTime startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(final LocalTime endTime) {
		this.endTime = endTime;
	}

	public void setIntervalTime(final int intervalTime) {
		this.intervalTime = intervalTime;
	}

	public void setStations(final Set<LineStation> stations) {
		this.stations = stations;
	}

	public void setCreatedAt(final LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setUpdatedAt(final LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
