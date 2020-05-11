package wooteco.subway.admin.domain;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.Id;

import wooteco.subway.admin.exception.DuplicateLineStationException;
import wooteco.subway.admin.exception.NotFoundPreStationException;
import wooteco.subway.admin.exception.NotFoundStationException;

public class Line {
	@Id
	private Long id;
	private String name;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private String bgColor;
	private List<LineStation> stations = new LinkedList<>();
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private Line() {
	}

	public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
		LocalDateTime createdTime = LocalDateTime.now();
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.bgColor = bgColor;
		this.createdAt = createdTime;
		this.updatedAt = createdTime;
	}

	public void update(Line line) {
		this.name = line.getName();
		this.startTime = line.getStartTime();
		this.endTime = line.getEndTime();
		this.intervalTime = line.getIntervalTime();
		this.bgColor = line.getBgColor();
		this.updatedAt = LocalDateTime.now();
	}

	public void addLineStation(LineStation lineStation) {
		validateDuplicateLineStationId(lineStation);
		if (stations.isEmpty()) {
			stations.add(lineStation);
			return;
		}
		if (isFirstInsert(lineStation)) {
			updateNextAndInsertNode(0, lineStation);
			return;
		}
		addIfNotDepart(lineStation);
	}

	private boolean isFirstInsert(LineStation lineStation) {
		return lineStation.getPreStationId() == null;
	}

	private void validateDuplicateLineStationId(LineStation lineStation) {
		boolean isAlreadyExistStationId = stations.stream()
			.anyMatch(station -> station.hasSameStationId(lineStation));
		if (isAlreadyExistStationId) {
			throw new DuplicateLineStationException();
		}
	}

	private void updateNextAndInsertNode(int index, LineStation newNode) {
		LineStation nextNode = stations.get(index);
		nextNode.updatePreLineStation(newNode.getStationId());
		stations.add(index, newNode);
	}

	private void addIfNotDepart(LineStation newNode) {
		if (stations.isEmpty() || isFirstInsert(newNode)) {
			return;
		}
		LineStation prevNode = findPrevLineStationFrom(newNode);
		int newNodeIndex = stations.indexOf(prevNode) + 1;
		if (newNodeIndex < stations.size()) {
			updateNextAndInsertNode(newNodeIndex, newNode);
			return;
		}
		stations.add(newNode);
	}

	private LineStation findPrevLineStationFrom(LineStation node) {
		return stations.stream()
			.filter(station -> station.getStationId().equals(node.getPreStationId()))
			.findFirst()
			.orElseThrow(NotFoundPreStationException::new);
	}

	public void removeLineStationById(Long targetStationId) {
		LineStation target = findLineStationWithStationId(targetStationId);
		int targetIndex = stations.indexOf(target);
		if (isStationLastIndex(targetIndex)) {
			stations.remove(target);
			return;
		}
		LineStation nextNode = stations.get(targetIndex + 1);
		nextNode.updatePreLineStation(target.getPreStationId());
		stations.remove(target);
	}

	private LineStation findLineStationWithStationId(Long stationId) {
		return stations.stream()
			.filter(station -> station.getStationId().equals(stationId))
			.findFirst()
			.orElseThrow(NotFoundStationException::new);
	}

	private boolean isStationLastIndex(int index) {
		return index == stations.size() - 1;
	}

	public List<Long> findLineStationIds() {
		return stations.stream()
			.map(LineStation::getStationId)
			.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}

	public boolean isNotSameName(Line otherLine) {
		return !name.equals(otherLine.name);
	}

	public List<Station> findContainingStationsFrom(List<Station> allStations) {
		return stations.stream()
			.map(lineStation -> mapToRightStation(lineStation, allStations))
			.collect(toList());
	}

	private Station mapToRightStation(LineStation lineStation, List<Station> stationCandidates) {
		return stationCandidates.stream()
			.filter(station -> station.hasSameId(lineStation.getStationId()))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("인자값 중, 현재 노선이 포함하는 역이 없습니다."));
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

	public List<LineStation> getStations() {
		return stations;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public String getBgColor() {
		return bgColor;
	}
}
