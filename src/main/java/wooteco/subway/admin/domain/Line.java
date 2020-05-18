package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class Line {
	private static final Long PRESTATION_ID_OF_FIRST_LINESTATION = 0L;
	private static final int INDEX_OF_FIRST_LINESTATION = 0;
	private static final int GAP_TO_NEXT_INDEX = 1;
	@Id
	private Long id;
	private String name;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private String color;
	@MappedCollection
	private List<LineStation> lineStations;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Line() {
	}

	public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime,
		String color) {
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.color = color;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		this.lineStations = new ArrayList<>();
	}

	public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime,
		String color) {
		this(null, name, startTime, endTime, intervalTime, color);
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

	public List<LineStation> getLineStations() {
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
		if (line.getLineStations() != null) {
			this.lineStations = line.getLineStations();
		}

		this.updatedAt = LocalDateTime.now();
	}

	public void addLineStation(LineStation lineStationToAdd) {
		if (lineStationToAdd.isFirstLineStation()) {
			if (!lineStations.isEmpty()) {
				updateNextLineStationWhenAdd(lineStationToAdd);
			}
			lineStations.add(INDEX_OF_FIRST_LINESTATION, lineStationToAdd);
			return;
		}

		updateNextLineStationWhenAdd(lineStationToAdd);
		LineStation preLineStation = findPreLineStation(lineStationToAdd);

		int indexToAdd = lineStations.indexOf(preLineStation) + GAP_TO_NEXT_INDEX;
		lineStations.add(indexToAdd, lineStationToAdd);
	}

	private void updateNextLineStationWhenAdd(LineStation lineStationToAdd) {
		lineStations.stream()
			.filter(lineStation -> lineStation.ifAfter(lineStationToAdd))
			.findFirst()
			.ifPresent(lineStation -> lineStation.updatePreStationId(lineStationToAdd.getStationId()));
	}

	private LineStation findPreLineStation(LineStation lineStationToAdd) {
		return lineStations.stream()
			.filter(lineStation -> lineStation.isBefore(lineStationToAdd))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("추가할 구간의 preStationId와 자신의 StationId가 같은 구간이 존재하지 않습니다."));
	}

	public void removeLineStationById(Long stationId) {
		LineStation lineStationToRemove = lineStations.stream()
			.filter(lineStation -> lineStation.equalsStationId(stationId))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("주어진 StationId를 가진 구간이 존재하지 않습니다."));

		if (lineStationToRemove.isFirstLineStation()) {
			updateNextLineStationWhenRemove(lineStationToRemove, PRESTATION_ID_OF_FIRST_LINESTATION);
			lineStations.remove(lineStationToRemove);
			return;
		}

		int indexOfPreLineStation = lineStations.indexOf(lineStationToRemove) - GAP_TO_NEXT_INDEX;
		LineStation preLineStation = lineStations.get(indexOfPreLineStation);

		updateNextLineStationWhenRemove(lineStationToRemove, preLineStation.getStationId());

		lineStations.remove(lineStationToRemove);
	}

	private void updateNextLineStationWhenRemove(LineStation lineStationToRemove, Long preStationIdToUpdate) {
		lineStations.stream()
			.filter(lineStation -> lineStation.ifAfter(lineStationToRemove))
			.findFirst()
			.ifPresent(lineStation -> lineStation.updatePreStationId(preStationIdToUpdate));
	}

	public List<Long> getAllStationIds() {
		return lineStations.stream()
			.mapToLong(LineStation::getStationId)
			.boxed()
			.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "Line{" +
			"id=" + id +
			", name='" + name + '\'' +
			", color='" + color + '\'' +
			", lineStations=" + lineStations +
			'}';
	}
}
