package wooteco.subway.admin.domain;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.data.annotation.Id;

public class Line {
	private static final Long NOT_EXIST = null;

	@Id
	private Long id;
	private String title;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private String bgColor;
	private Set<LineStation> stations = new LinkedHashSet<>();
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Line() {
	}

	public Line(Long id, String title, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
		this.id = id;
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.bgColor = bgColor;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Line(Long id, String title, LocalTime startTime, LocalTime endTime, int intervalTime) {
		this(id, title, startTime, endTime, intervalTime, "bgColor");
	}

	public Line(String title, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
		this(null, title, startTime, endTime, intervalTime, bgColor);
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
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

	public String getBgColor() {
		return bgColor;
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

	public void update(Line line) {
		if (line.getTitle() != null) {
			this.title = line.getTitle();
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
		if (isHeadLineStation(lineStation) && isNotEmptyStation()) {
			LineStation prevFirstLineStation = findLineStationWith(this::isHeadLineStation);
			prevFirstLineStation.updatePreLineStation(lineStation.getStationId());
		} else if (isNotEmptyStation()) {
			stations.stream()
				.filter(value -> value.getPreStationId() == lineStation.getPreStationId())
				.findFirst()
				.ifPresent(station -> station.updatePreLineStation(lineStation.getStationId()));
		}
		stations.add(lineStation);
	}

	public void removeLineStationById(Long stationId) {
		LineStation removeStation = findLineStationWith(value -> stationId.equals(value.getStationId()));
		Long headStationId = findLineStationWith(this::isHeadLineStation).getStationId();

		if (isOnlyOneStationInLine()) {
			stations.remove(removeStation);
			return;
		}

		if (stationId.equals(headStationId)) {
			stations.remove(removeStation);
			LineStation newHeadStation = findLineStationWith(value -> stationId.equals(value.getPreStationId()));
			newHeadStation.updatePreLineStation(NOT_EXIST);
			return;
		}

		stations.stream()
			.filter(value -> stationId.equals(value.getPreStationId()))
			.findFirst()
			.ifPresent(lineStation -> lineStation.updatePreLineStation(removeStation.getPreStationId()));
		stations.remove(removeStation);
	}

	public List<Long> getLineStationsId() {
		List<Long> newStations = new ArrayList<>();

		if (!stations.isEmpty()) {
			LineStation headStation = findLineStationWith(this::isHeadLineStation);
			newStations.add(headStation.getStationId());
		}

		Map<Long, Long> lineStations = stations.stream()
			.collect(toMap(LineStation::getPreStationId, LineStation::getStationId));

		while (newStations.size() != stations.size()) {
			Long lastStationId = newStations.get(newStations.size() - 1);
			newStations.add(lineStations.get(lastStationId));
		}

		return newStations;
	}

	private LineStation findLineStationWith(Predicate<LineStation> expression) {
		return stations.stream()
			.filter(expression)
			.findFirst()
			.orElseThrow(NoSuchElementException::new);
	}

	private boolean isHeadLineStation(LineStation lineStation) {
		return lineStation.getPreStationId() == NOT_EXIST;
	}

	private boolean isNotEmptyStation() {
		return !stations.isEmpty();
	}

	private boolean isOnlyOneStationInLine() {
		return stations.size() == 1;
	}

	public boolean isSameTitle(Line that) {
		return title.equals(that.title);
	}
}
