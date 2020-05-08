package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
		if (lineStation.getPreStationId() == null && stations.size() != 0) {
			LineStation startLineStation = stations.stream()
				.filter(station -> station.getPreStationId() == null)
				.findFirst().orElseThrow(RuntimeException::new);
			startLineStation.updatePreLineStation(lineStation.getStationId());
		}
		stations.stream()
			.filter(station -> lineStation.getPreStationId() == station.getPreStationId())
			.findFirst()
			.ifPresent(station -> station.updatePreLineStation(lineStation.getStationId()));
		stations.add(lineStation);
	}

	public void removeLineStationById(Long stationId) {
		LineStation lineStation = stations.stream()
			.filter(station -> station.getStationId() == stationId)
			.findFirst().orElseThrow(RuntimeException::new);
		stations.stream()
			.filter(station -> station.getPreStationId() == stationId)
			.findFirst()
			.ifPresent(nextLineStation -> nextLineStation.updatePreLineStation(lineStation.getPreStationId()));
		stations.remove(lineStation);
	}

	public List<Long> getLineStationsId() {
		List<Long> lineStationsId = new ArrayList<>();
		LineStation startLineStation = stations.stream()
			.filter(station -> station.getPreStationId() == null)
			.findFirst().orElseThrow(RuntimeException::new);

		Queue<Long> queue = new LinkedList<>();
		queue.add(startLineStation.getStationId());

		while (!queue.isEmpty()) {
			Long l = queue.poll();
			lineStationsId.add(l);
			System.out.println(l);
			stations.stream()
				.filter(station -> l.equals(station.getPreStationId()))
				.findFirst()
				.ifPresent(lineStation -> queue.add(lineStation.getStationId()));
		}
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
}
