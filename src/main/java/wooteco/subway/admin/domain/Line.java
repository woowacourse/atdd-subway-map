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

	public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime) {
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime) {
		this(null, name, startTime, endTime, intervalTime);
	}

	public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
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

		this.updatedAt = LocalDateTime.now();
	}

	public void addLineStation(LineStation lineStation) {
		if (stations.isEmpty()) {
			stations.add(lineStation);
			return;
		}

		if (lineStation.getPreStationId() == null) {
			LineStation firstLineStation = stations.get(0);
			firstLineStation.modifyPreStationId(lineStation.getStationId());
			stations.add(0, lineStation);
			return;
		}

		LineStation targetLineStation = stations.stream()
			.filter(station -> station.getStationId().equals(lineStation.getPreStationId()))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("연결할 수 있는 역이 없습니다."));
		int nextLineStationIndex = stations.indexOf(targetLineStation) + 1;

		if (nextLineStationIndex < stations.size() - 1) {
			LineStation nextLineStation = stations.get(nextLineStationIndex);
			nextLineStation.modifyPreStationId(lineStation.getStationId());
			stations.add(nextLineStationIndex, lineStation);
			stations.set(nextLineStationIndex + 1, nextLineStation);
			return;
		}

		stations.add(lineStation);
	}

	public void removeLineStationById(Long stationId) {
		if (stations.isEmpty()) {
			throw new IllegalStateException();
		}

		if (stations.size() == 1) {
			stations.clear();
			return;
		}

		LineStation targetLineStation = stations.stream()
			.filter(station -> station.getStationId().equals(stationId))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("연결할 수 있는 역이 없습니다."));
		int nextLineStationIndex = stations.indexOf(targetLineStation) + 1;

		if (targetLineStation.getPreStationId() == null) {
			LineStation nextLineStation = stations.get(nextLineStationIndex);
			nextLineStation.modifyPreStationId(null);
			stations.set(nextLineStationIndex, nextLineStation);
			stations.remove(targetLineStation);
			return;
		}

		if (nextLineStationIndex <= stations.size() - 1) {
			LineStation nextLineStation = stations.get(nextLineStationIndex);
			LineStation preLineStation = stations.get(nextLineStationIndex - 1);
			nextLineStation.modifyPreStationId(preLineStation.getStationId());
			stations.set(nextLineStationIndex, nextLineStation);
			stations.remove(targetLineStation);
			return;
		}

		stations.remove(targetLineStation);
	}

	public List<Long> findLineStationsId() {
		return stations.stream()
			.map(LineStation::getStationId)
			.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}
}
