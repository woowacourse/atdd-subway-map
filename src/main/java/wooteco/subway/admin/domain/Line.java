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
	private Set<Edge> stations;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Line() {
	}

	public Line(String name, String color, LocalTime startTime, LocalTime endTime,
			int intervalTime) {
		this(null, name, color, startTime, endTime, intervalTime);
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

	public void addEdge(Edge edge) {
		stations.stream()
				.filter(st -> Objects.equals(st.getPreStationId(), edge.getPreStationId()))
				.findFirst()
				.ifPresent(st -> st.updatePreEdge(edge.getStationId()));
		stations.add(edge);
	}

	public Edge removeEdgeById(Long stationId) {
		Edge target = stations.stream()
				.filter(st -> st.isEqualsWithStationId(stationId))
				.findFirst()
				.orElseThrow(NoSuchElementException::new);

		stations.stream()
				.filter(st -> st.isEqualsWithPreStationId(stationId))
				.findFirst()
				.ifPresent(st -> st.updatePreEdge(target.getPreStationId()));

		stations.remove(target);
		return target;
	}

	public List<Long> getEdgesId() {
		List<Edge> list = new ArrayList<>();
		stations.stream()
				.filter(lineStation -> lineStation.getPreStationId() == null)
				.findFirst()
				.ifPresent(list::add);

		for (int i = 0; i < stations.size() - 1; i++) {
			list.add(findNext(list.get(i)));
		}
		return list.stream()
				.map(Edge::getStationId)
				.collect(Collectors.toList());
	}

	private Edge findNext(Edge nextStation) {
		return stations.stream()
				.filter(lineStation -> Objects.equals(
						lineStation.getPreStationId(), nextStation.getStationId()))
				.findFirst()
				.orElse(nextStation);
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

	public Set<Edge> getStations() {
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
