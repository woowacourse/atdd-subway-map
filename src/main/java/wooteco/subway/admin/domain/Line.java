package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.annotation.Id;

public class Line {
	@Id
	private final Long id;
	private final String name;
	private final String color;
	private final LocalTime startTime;
	private final LocalTime endTime;
	private final int intervalTime;
	private final Set<LineStation> stations;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	Line(Long id, String name, String color, LocalTime startTime, LocalTime endTime, int intervalTime,
		Set<LineStation> stations, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.stations = stations;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static Line of(String name, String color, LocalTime startTime, LocalTime endTime, int intervalTime,
		Set<LineStation> stations) {
		return new Line(null, name, color, startTime, endTime, intervalTime, stations, LocalDateTime.now(),
			LocalDateTime.now());
	}

	public Line update(Line line) {
		return new Line(this.id, line.getName(), line.getColor(), line.getStartTime(), line.getEndTime(),
			line.getIntervalTime(), this.stations, this.createdAt, LocalDateTime.now());
	}

	public void addLineStation(LineStation lineStation) {
		if (alreadyExists(lineStation)) {
			throw new UnsupportedOperationException("이미 존재하는 구간입니다.");
		}
		if (lineStation.isStartStation() && stations.size() != 0) {
			stations.stream()
				.filter(LineStation::isStartStation)
				.findFirst()
				.ifPresent(station -> {
					LineStation newStation = station.updatePreLineStation(lineStation.getStationId());
					stations.remove(station);
					stations.add(newStation);
				});
		}
		stations.stream()
			.filter(station -> Objects.nonNull(lineStation.getPreStationId()) && lineStation.getPreStationId()
				.equals(station.getPreStationId())).findFirst()
			.ifPresent(station -> {
				LineStation newStation = station.updatePreLineStation(lineStation.getStationId());
				stations.remove(station);
				stations.add(newStation);
			});
		stations.add(lineStation);
	}

	private boolean alreadyExists(LineStation lineStation) {
		return stations.stream()
			.anyMatch(station -> (station.getPreStationId().equals(lineStation.getPreStationId()))
				&& (station.getStationId().equals(lineStation.getStationId())));
	}

	public void removeLineStationById(Long stationId) {
		LineStation target = null;
		LineStation next = null;

		for (LineStation lineStation : stations) {
			if (stationId.equals(lineStation.getStationId())) {
				target = lineStation;
			}
			if (stationId.equals(lineStation.getPreStationId())) {
				next = lineStation;
			}
		}
		if (Objects.isNull(target)) {
			throw new RuntimeException("존재하지 않는 역입니다.");
		}
		stations.remove(target);
		if (Objects.nonNull(next)) {
			stations.add(next.updatePreLineStation(target.getPreStationId()));
			stations.remove(next);
		}
	}

	public List<Long> getLineStationsId() {
		List<Long> lineStationsId = new ArrayList<>();

		Optional<LineStation> startStation = findStart();
		if (!startStation.isPresent()) {
			throw new UnsupportedOperationException("출발역이 없습니다.");
		}

		Long currentId = startStation.get().getStationId();
		while (Objects.nonNull(currentId)) {
			lineStationsId.add(currentId);
			Optional<LineStation> next = findNext(currentId);
			if (next.isPresent()) {
				currentId = next.get().getStationId();
				continue;
			}
			currentId = null;
		}
		return lineStationsId;
	}

	private Optional<LineStation> findNext(final Long currentId) {
		return stations.stream()
			.filter(station -> currentId.equals(station.getPreStationId()))
			.findFirst();
	}

	private Optional<LineStation> findStart() {
		return stations.stream()
			.filter(station -> station.getPreStationId() == null)
			.findFirst();
	}

	public Line withId(final Long id) {
		return new Line(id, this.name, this.color, this.startTime, this.endTime, this.intervalTime, this.stations,
			this.createdAt, this.updatedAt);
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

	public Set<LineStation> getStations() {
		return stations;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
