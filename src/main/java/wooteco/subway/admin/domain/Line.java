package wooteco.subway.admin.domain;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.Id;

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

	public Line() {
	}

	public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.bgColor = bgColor;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
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
		if (line.getBgColor() != null) {
			this.bgColor = line.getBgColor();
		}
		this.updatedAt = LocalDateTime.now();
	}

	public void addLineStation(LineStation lineStation) {
		validateDuplicateLineStationId(lineStation);
		if (stations.isEmpty()) {
			stations.add(lineStation);
			return;
		}
		if (lineStation.getPreStationId() == null) {
			updateNextAndInsertNode(0, lineStation);
			return;
		}
		addIfStationIsNotDepart(lineStation);
	}

	private void validateDuplicateLineStationId(LineStation lineStation) {
		boolean isAlreadyExistStationId = stations.stream()
			.anyMatch(station -> station.hasSameStationId(lineStation));
		if (isAlreadyExistStationId) {
			throw new IllegalArgumentException("이미 해당 구간에 포함된 역입니다.");
		}
	}

	private void updateNextAndInsertNode(int index, LineStation lineStation) {
		LineStation newLineStation = stations.get(index);
		newLineStation.updatePreLineStation(lineStation.getStationId());
		stations.add(index, lineStation);
	}

	private void addIfStationIsNotDepart(LineStation lineStation) {
		if (stations.isEmpty() || lineStation.getPreStationId() == null) {
			return;
		}
		LineStation prevLineStation = findPrevLineStationFrom(lineStation);
		int newIndex = stations.indexOf(prevLineStation) + 1;
		if (newIndex < stations.size()) {
			updateNextAndInsertNode(newIndex, lineStation);
			return;
		}
		stations.add(lineStation);
	}

	private LineStation findPrevLineStationFrom(LineStation lineStation) {
		return stations.stream()
			.filter(station -> station.getStationId().equals(lineStation.getPreStationId()))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("연결할 수 있는 역이 없습니다."));
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
			.orElseThrow(() -> new IllegalStateException("해당 역이 없습니다."));
	}

	private boolean isStationLastIndex(int index) {
		return index == stations.size() - 1;
	}

	public List<Long> findLineStationsId() {
		return stations.stream()
			.map(LineStation::getStationId)
			.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}
}
