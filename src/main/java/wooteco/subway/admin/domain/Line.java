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
	private final Long id;
	private final String name;
	private final LocalTime startTime;
	private final LocalTime endTime;
	private final int intervalTime;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;
	private final String colorType;
	private final String colorValue;

	@MappedCollection(idColumn = "line", keyColumn = "sequence")
	private final List<Edge> edges;

	Line(final Long id, final String name, final LocalTime startTime,
		final LocalTime endTime, final int intervalTime, final String colorType,
		final String colorValue, final List<Edge> edges, final LocalDateTime createdAt,
		final LocalDateTime updatedAt) {

		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.colorType = colorType;
		this.colorValue = colorValue;
		this.edges = edges;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static Line of(String name, LocalTime startTime, LocalTime endTime,
		int intervalTime, String bgColor) {
		LocalDateTime localDateTime = LocalDateTime.now();
		Color color = Color.ofBgColor(bgColor);

		return new Line(null, name, startTime, endTime, intervalTime, color.getType(),
			color.getValue(), new LinkedList<>(), localDateTime, localDateTime);
	}

	public Line withId(Long id) {
		return new Line(id, this.name, this.startTime, this.endTime, this.intervalTime,
			this.colorType, this.colorValue, this.edges, this.createdAt, this.updatedAt);
	}

	public Line update(Line line) {
		return new Line(this.id, line.name, line.startTime, line.endTime,
			line.intervalTime, line.colorType, line.colorValue, line.edges,
			this.createdAt, line.updatedAt);
	}

	public void addEdge(Edge edge) {
		validateDuplication(edge);

		if (edges.isEmpty() && edge.isNotStartEdge()) {
			edges.add(Edge.starter(edge.getPreStationId()));
			edges.add(edge);
			return;
		}

		int index = findIndex(edge);
		edges.add(index, edge);

		if (index < getEndIndexOfEdges()) {
			edges.set(index + ONE, edges.get(index + ONE)
				.updatePreStationId(edge.getStationId()));
		}
	}

	private void validateDuplication(Edge edge) {
		boolean isDuplicated = edges.stream()
			.anyMatch(item -> item.equalsStationId(edge.getStationId()));

		if (isDuplicated) {
			throw new IllegalArgumentException("구간은 중복될 수 없고, "
				+ "하나의 역은 두 개의 이전역을 가질 수 없습니다.");
		}
	}

	private int findIndex(Edge edge) {
		if (edge.isStartEdge()) {
			return 0;
		}
		return edges.stream()
			.filter(item -> item.equalsStationId(edge.getPreStationId()))
			.findFirst()
			.map(item -> edges.indexOf(item) + ONE)
			.orElseThrow(() -> new IllegalArgumentException("이전 역이 존재하지 않습니다."));
	}

	private int getEndIndexOfEdges() {
		return edges.size() - 1;
	}

	public void removeEdgeById(Long stationId) {
		int index = edges.stream()
			.filter(edge -> edge.equalsStationId(stationId))
			.findFirst()
			.map(edges::indexOf)
			.orElseThrow(() -> new IllegalArgumentException("지우려는 역이 존재하지 않습니다."));

		Edge removeEdge = edges.remove(index);

		if (edges.isEmpty()) {
			return;
		}

		if (removeEdge.isStartEdge()) {
			Edge firstEdge = edges.get(FIRST_INDEX);
			edges.set(FIRST_INDEX,
				firstEdge.updatePreStationId(firstEdge.getStationId()));
			return;
		}

		if (index < edges.size() - ONE) {
			edges.set(index, edges.get(index)
				.updatePreStationId(edges.get(index - ONE).getStationId()));
		}
	}

	public List<Long> getEdgesId() {
		return edges.stream()
			.map(Edge::getStationId)
			.collect(Collectors.toList());
	}

	public String getBgColor() {
		return Color.ofBgColor(colorType, colorValue).toBgColor();
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

	public String getColorType() {
		return colorType;
	}

	public String getColorValue() {
		return colorValue;
	}

	@Override
	public String toString() {
		return "Line{" +
			"id=" + id +
			", name='" + name + '\'' +
			", startTime=" + startTime +
			", endTime=" + endTime +
			", intervalTime=" + intervalTime +
			", createdAt=" + createdAt +
			", updatedAt=" + updatedAt +
			", colorType='" + colorType + '\'' +
			", colorValue='" + colorValue + '\'' +
			", edges=" + edges +
			'}';
	}
}
