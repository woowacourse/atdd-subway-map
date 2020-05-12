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
import org.springframework.data.relational.core.mapping.MappedCollection;

public class Line {
	@Id
	private Long id;
	private String name;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private String color;
	@MappedCollection(idColumn = "line_id")
	private Set<Edge> edges = new HashSet<>();
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
			edges.add(new Edge(null, edge.getPreStationId(), 0, 0));
			edges.add(edge);
			return;
		}

		if (isFinalEdge(edge)) {
			edges.add(edge);
			return;
		}

		if (isFirstEdge(edge)) {
			edges.stream()
					.filter(item -> item.getPreStationId() == null)
					.findFirst()
					.ifPresent(item -> item.update(new Edge(edge.getStationId(), item.getStationId(),
							0, 0)));
			edges.add(edge);
			return;
		}

		edges.stream()
				.filter(item -> Objects.equals(item.getPreStationId(), edge.getPreStationId()))
				.findFirst()
				.ifPresent(item -> item.update(new Edge(edge.getStationId(), item.getStationId(),
						item.getDistance(), item.getDuration())));

		edges.add(edge);
	}

	private boolean isFinalEdge(final Edge edge) {
		try {
			Edge previous = edges.stream()
				.filter(item -> item.stationIdEquals(edge.getPreStationId()))
					.findFirst()
					.orElseThrow(NoSuchElementException::new);
			return edges.stream()
				.noneMatch(item -> item.preStationIdEquals(previous.getStationId()));
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	private boolean isFirstEdge(final Edge edge) {
		return edge.getPreStationId() == null;
	}

	public void removeEdgeById(Long stationId) {
		updateNextEdge(stationId);

		edges = edges.stream()
			.filter(edge -> !edge.stationIdEquals(stationId))
				.collect(Collectors.toSet());
	}

	private void updateNextEdge(final Long stationId) {
		try {
			Edge nextEdge = edges.stream()
				.filter(edge -> edge.preStationIdEquals(stationId))
					.findFirst()
					.orElseThrow(NoSuchElementException::new);
			Long afterPreStationIdOfNextEdge = edges.stream()
				.filter(edge -> edge.stationIdEquals(stationId))
					.findFirst()
					.orElseThrow(NoSuchElementException::new)
					.getPreStationId();
			nextEdge.update(new Edge(afterPreStationIdOfNextEdge, nextEdge.getStationId(),
					nextEdge.getDistance(), nextEdge.getDuration()));
		} catch (NoSuchElementException ignored) {
		}
	}

	public List<Long> getEdgesId() {
		List<Long> edgeIds = new ArrayList<>();
		Long preStation = null;
		while (true) {
			try {
				final Long preStationOfNext = preStation;
				Edge nextEdge = edges.stream()
					.filter(edge -> edge.preStationIdEquals(preStationOfNext))
						.findFirst()
						.orElseThrow(NoSuchElementException::new);
				edgeIds.add(nextEdge.getStationId());
				preStation = nextEdge.getStationId();
			} catch (NoSuchElementException ignored) {
				break;
			}
		}

		return edgeIds;
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

	public Set<Edge> getEdges() {
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
