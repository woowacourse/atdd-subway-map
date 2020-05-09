package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import org.springframework.data.annotation.Id;

public class Line {
	@Id
	private final Long id;
	private final String name;
	private final String color;
	private final LocalTime startTime;
	private final LocalTime endTime;
	private final int intervalTime;
	private final Set<LineStation> stations;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	Line(Long id, String name, String color, LocalTime startTime, LocalTime endTime, int intervalTime,
		Set<LineStation> stations, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.stations = stations;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static Line of(String name, String color, LocalTime startTime, LocalTime endTime, int intervalTime,
		Set<LineStation> stations) {
		return new Line(null, name, color, startTime, endTime, intervalTime, stations, LocalDateTime.now(),
			LocalDateTime.now());
	}

	public Line update(Line line) {
		return new Line(this.id, line.getName(), line.getColor(), line.getStartTime(), line.getEndTime(),
			line.getIntervalTime(), this.stations, this.createdAt, LocalDateTime.now());
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

	public Line withId(final Long id) {
		return new Line(id, this.name, this.color, this.startTime, this.endTime, this.intervalTime, this.stations,
			this.createdAt, this.updatedAt);
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
}
