package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;

public class Line {
	@Id
	private Long id;
	private String name;
	private String color;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private Set<LineStation> stations;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Line() {
	}

	public Line(Long id, String name, String color, LocalTime startTime, LocalTime endTime,
			int intervalTime) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.stations = new HashSet<>();
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Line(String name, String color, LocalTime startTime, LocalTime endTime,
			int intervalTime) {
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
		stations.stream()
				.filter(st -> Objects.equals(st.getPreStationId(), lineStation.getPreStationId()))
				.findFirst()
				.ifPresent(st -> st.updatePreLineStation(lineStation.getStationId()));
		stations.add(lineStation);
	}

	public LineStation removeLineStationById(Long stationId) {
		LineStation lineStation = stations.stream()
				.filter(st -> st.getStationId().equals(stationId))
				.findFirst()
				.orElseThrow(NoSuchElementException::new);
		stations.remove(lineStation);

		stations.stream()
				.filter(st -> Objects.equals(st.getPreStationId(), stationId))
				.findFirst()
				.ifPresent(st -> st.updatePreLineStation(lineStation.getPreStationId()));
		return lineStation;
	}

	public List<Long> getLineStationsId() {
		List<LineStation> list = new ArrayList<>();
		stations.stream()
				.filter(lineStation -> lineStation.getPreStationId() == null)
				.findFirst()
				.ifPresent(list::add);

		for (int i = 0; i < stations.size() - 1; i++) {
			list.add(findNext(list.get(i)));
		}
		return list.stream()
				.map(LineStation::getStationId)
				.collect(Collectors.toList());
	}

	private LineStation findNext(LineStation nextStation) {
		return stations.stream()
				.filter(lineStation -> Objects.equals(
						lineStation.getPreStationId(), nextStation.getStationId()))
				.findFirst()
				.orElse(nextStation);
	}

	public List<Long> stationsId() {
		return stations.stream()
				.map(LineStation::getStationId)
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

	public Set<LineStation> getStations() {
		return stations;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public String toString() {
		return "Line{" +
				"id=" + id +
				", name='" + name + '\'' +
				", color='" + color + '\'' +
				", startTime=" + startTime +
				", endTime=" + endTime +
				", intervalTime=" + intervalTime +
				", stations=" + stations +
				", createdAt=" + createdAt +
				", updatedAt=" + updatedAt +
				'}';
	}
}
