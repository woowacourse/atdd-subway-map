package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import wooteco.subway.admin.exception.LineStationException;

public class Line {

	@Id
	@Column("id")
	private Long id;

	@Column("name")
	private String name;

	@Column("start_time")
	private LocalTime startTime;

	@Column("end_time")
	private LocalTime endTime;

	@Column("interval_time")
	private int intervalTime;

	@Column("bg_color")
	private String bgColor;

	@Column("created_at")
	private LocalDateTime createdAt;

	@Column("updated_at")
	private LocalDateTime updatedAt;

	private Set<LineStation> stations = new LinkedHashSet<>();

	public Line() {
	}

	public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime,
		String bgColor) {
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.bgColor = bgColor;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime,
		String bgColor) {
		this(null, name, startTime, endTime, intervalTime, bgColor);
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
		if (Objects.nonNull(line.getName())) {
			this.name = line.getName();
		}
		if (Objects.nonNull(line.getStartTime())) {
			this.startTime = line.getStartTime();
		}
		if (Objects.nonNull(line.getEndTime())) {
			this.endTime = line.getEndTime();
		}
		if (line.getIntervalTime() != 0) {
			this.intervalTime = line.getIntervalTime();
		}
		if (Objects.nonNull(line.getBgColor())) {
			this.bgColor = line.getBgColor();
		}
		this.updatedAt = LocalDateTime.now();
	}

	public void addLineStation(LineStation lineStation) {
		if (stations.isEmpty()) {
			if (Objects.nonNull(lineStation.getPreStationId())) {
				throw new LineStationException("출발역이 존재하지 않습니다.");
			}
			stations.add(lineStation);
			return;
		}
		if (isHeadStation(lineStation)) {
			final LineStation headLineStation = findHeadLineStation();
			headLineStation.updatePreLineStation(lineStation.getStationId());
			stations.add(lineStation);
			return;
		}
		final Optional<LineStation> lineStationByPreStationId = findLineStationByPreStationId(
			lineStation.getPreStationId());
		if (lineStationByPreStationId.isPresent()) {
			lineStationByPreStationId.get().updatePreLineStation(lineStation.getStationId());
			stations.add(lineStation);
			return;
		}
		stations.add(lineStation);
	}

	private boolean isHeadStation(LineStation lineStation) {
		return Objects.isNull(lineStation.getPreStationId());
	}

	private LineStation findHeadLineStation() {
		return stations.stream()
		               .filter(lineStation -> Objects.isNull(lineStation.getPreStationId()))
		               .findFirst()
		               .orElseThrow(() -> new LineStationException("출발역이 존재하지 않습니다."));
	}

	private Optional<LineStation> findLineStationByPreStationId(Long preStationId) {
		return stations.stream()
		               .filter(lineStation -> preStationId.equals(lineStation.getPreStationId()))
		               .findFirst();
	}

	public void removeLineStationById(Long stationId) {
		final LineStation removeLineStation = findLineStationByStationId(stationId)
			.orElseThrow(() -> new LineStationException("삭제하려는 역이 존재하지 않습니다."));

		if (isHeadStation(removeLineStation)) {
			stations.remove(removeLineStation);
			findLineStationByPreStationId(stationId)
				.ifPresent(lineStation -> lineStation.updatePreLineStation(null));
			return;
		}
		stations.remove(removeLineStation);
		findLineStationByPreStationId(stationId)
			.ifPresent(lineStation -> lineStation
				.updatePreLineStation(removeLineStation.getPreStationId()));
	}

	private Optional<LineStation> findLineStationByStationId(Long stationId) {
		return stations.stream()
		               .filter(lineStation -> stationId.equals(lineStation.getStationId()))
		               .findFirst();
	}

	public List<Long> getLineStationsId() {
		final List<Long> stationIds = new ArrayList<>();

		if (stations.isEmpty()) {
			return stationIds;
		}

		final LineStation headLineStation = findHeadLineStation();
		stationIds.add(headLineStation.getStationId());

		while (stationIds.size() != stations.size()) {
			final Long stationId =
				findLineStationByPreStationId(stationIds.get(stationIds.size() - 1))
					.map(LineStation::getStationId)
					.orElseThrow(() -> new LineStationException("라인의 구간에 오류가 존재합니다."));
			stationIds.add(stationId);
		}
		return stationIds;
	}
}
