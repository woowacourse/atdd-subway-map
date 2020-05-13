package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class Line {
	private static final int ONE = 1;
	private static final int FIRST_INDEX = 0;

	@Id
	private Long id;
	private String name;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private String color;
	@MappedCollection(idColumn = "line_id", keyColumn = "sequence")
	private List<Edge> edges = new LinkedList<>();
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private Line() {
	}

	public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, final String color) {
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.color = color;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, final String color) {
		this(null, name, startTime, endTime, intervalTime, color);
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

	public void addEdge(Edge edge) {
		if (edges.isEmpty() && edge.isNotStartEdge()) {
			edges.add(new Edge(edge.getPreStationId(), edge.getPreStationId(), 0, 0));
			edges.add(edge);
			return;
		}

		int index = findIndex(edge);
		edges.add(index, edge);

		if (index < getEndIndexOfEdges()) {
			edges.get(index + ONE).updatePreStationId(edge.getStationId());
		}
	}

	private int findIndex(Edge edge) {
		if (edge.isStartEdge()) {
			return 0;
		}
		return edges.stream()
			.filter(item -> item.stationIdEquals(edge.getPreStationId()))
			.findFirst()
			.map(item -> edges.indexOf(item) + ONE)
			.orElseThrow(IllegalArgumentException::new);
	}

	private int getEndIndexOfEdges() {
		return edges.size() - 1;
	}

	public void removeEdgeById(Long stationId) {
		int index = edges.stream()
			.filter(edge -> edge.stationIdEquals(stationId))
			.findFirst()
			.map(edge -> edges.indexOf(edge))
			.orElseThrow(() -> new IllegalArgumentException("지우려는 역이 존재하지 않습니다."));

		Edge removeEdge = edges.remove(index);

		if (edges.isEmpty()) {
			return;
		}

		if (removeEdge.isStartEdge()) {
			Edge newFirstEdge = edges.get(FIRST_INDEX);
			newFirstEdge.updatePreStationId(newFirstEdge.getStationId());
			return;
		}

		if (index < edges.size() - ONE) {
			edges.get(index).updatePreStationId(edges.get(index - ONE).getStationId());
		}
	}

	public List<Long> getEdgesId() {
		return edges.stream()
			.map(Edge::getStationId)
			.collect(Collectors.toList());
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

	public List<Edge> getEdges() {
		return edges;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public String getColor() {
		return color;
	}

	@Override
	public String toString() {
		return "Line{" +
			"id=" + id +
			", name='" + name + '\'' +
			", startTime=" + startTime +
			", endTime=" + endTime +
			", intervalTime=" + intervalTime +
			", color='" + color + '\'' +
			", edges=" + edges +
			", createdAt=" + createdAt +
			", updatedAt=" + updatedAt +
			'}';
	}
}
