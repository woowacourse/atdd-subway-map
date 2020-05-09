package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.data.annotation.Id;

public class Line {
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

	public Line(Long id, String title, LocalTime startTime, LocalTime endTime, int intervalTime,
		String bgColor) {
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

	public Line(String title, LocalTime startTime, LocalTime endTime, int intervalTime,
		String bgColor) {
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
		if (lineStation.getPreStationId() == null && stations.size() != 0) {
			LineStation prevFirstLineStation = stations.stream()
			                                           .filter(
				                                           value -> value.getPreStationId() == null)
			                                           .findFirst()
			                                           .orElseThrow(NoSuchElementException::new);
			prevFirstLineStation.updatePreLineStation(lineStation.getStationId());
		} else if (stations.size() != 0) {
			stations.stream()
			        .filter(value -> value.getPreStationId() == lineStation.getPreStationId())
			        .findFirst()
			        .ifPresent(station -> station.updatePreLineStation(lineStation.getStationId()));
		}
		stations.add(lineStation);
	}

	public void removeLineStationById(Long stationId) {
		LineStation removeStation = stations.stream()
		                                    .filter(value -> stationId.equals(value.getStationId()))
		                                    .findFirst()
		                                    .orElseThrow(NoSuchElementException::new);

		LineStation headStation = stations.stream()
		                                  .filter(value -> value.getPreStationId() == null)
		                                  .findFirst()
		                                  .orElseThrow(NoSuchElementException::new);

		if (stationId.equals(headStation.getStationId())) {
			stations.remove(removeStation);
			LineStation newHeadStation = stations.stream()
			                                     .filter(value -> stationId.equals(
				                                     value.getPreStationId()))
			                                     .findFirst()
			                                     .orElseThrow(NoSuchElementException::new);
			newHeadStation.updatePreLineStation(null);
			return;
		}

		stations.stream()
		        .filter(value -> stationId.equals(value.getPreStationId()))
		        .findFirst()
		        .ifPresent(lineStation -> lineStation.updatePreLineStation(
			        removeStation.getPreStationId()));
		stations.remove(removeStation);
	}

	public List<Long> getLineStationsId() {
		List<Long> newStations = new ArrayList<>();

		if (!stations.isEmpty()) {
			LineStation headStation = stations.stream()
			                                  .filter(value -> value.getPreStationId() == null)
			                                  .findFirst()
			                                  .orElseThrow(NoSuchElementException::new);
			newStations.add(headStation.getStationId());
		}

		while (newStations.size() != stations.size()) {
			newStations.add(stations.stream()
			                        .filter(
				                        value -> (newStations.get(newStations.size() - 1))
					                        .equals(value.getPreStationId()))
			                        .findFirst()
			                        .map(LineStation::getStationId)
			                        .orElseThrow(NoSuchElementException::new));
		}

		return newStations;
	}
}
