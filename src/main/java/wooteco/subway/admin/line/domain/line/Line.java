package wooteco.subway.admin.line.domain.line;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;

import wooteco.subway.admin.line.domain.edge.LineStation;
import wooteco.subway.admin.line.domain.edge.LineStationException;

public class Line {

	public static final int HEAD_LINE_STATION_INDEX = 0;
	public static final Long HEAD_STATION_ID = null;

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
	private Integer intervalTime;

	@Column("bg_color")
	private String bgColor;

	@Column("created_at")
	@CreatedDate
	private LocalDateTime createdAt;

	@Column("updated_at")
	@LastModifiedDate
	private LocalDateTime updatedAt;

	// TODO: 2020/05/12 LinkedList로 변경 후 관련 로직 수정하기
	@MappedCollection(idColumn = "line_id", keyColumn = "index")
	private List<LineStation> stations = new LinkedList<>();

	public Line() {
	}

	public Line(Long id, String name, LocalTime startTime, LocalTime endTime, Integer intervalTime,
		String bgColor) {
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.bgColor = bgColor;
	}

	public Line(String name, LocalTime startTime, LocalTime endTime, Integer intervalTime,
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

	public Integer getIntervalTime() {
		return intervalTime;
	}

	public String getBgColor() {
		return bgColor;
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
		if (Objects.nonNull(line.getIntervalTime())) {
			this.intervalTime = line.getIntervalTime();
		}
		if (Objects.nonNull(line.getBgColor())) {
			this.bgColor = line.getBgColor();
		}
		this.updatedAt = LocalDateTime.now();
	}

	public void addLineStation(LineStation lineStation) {
		final Long preStationId = lineStation.getPreStationId();

		if (stations.isEmpty()) {
			checkHeadStationByPreStationId(preStationId);
			stations.add(lineStation);
			return;
		}

		checkLineStationAlreadyExist(lineStation);

		if (Objects.isNull(preStationId)) {
			final LineStation headLineStation = stations.get(HEAD_LINE_STATION_INDEX);
			headLineStation.updatePreLineStation(lineStation.getStationId());
			stations.add(HEAD_LINE_STATION_INDEX, lineStation);
			return;
		}

		final Optional<LineStation> nextLineStation = findLineStationByPreStationId(preStationId);

		if (nextLineStation.isPresent()) {
			nextLineStation.get().updatePreLineStation(preStationId);
			stations.add(stations.indexOf(nextLineStation.get()), lineStation);
			return;
		}
		stations.add(lineStation);
	}

	private void checkHeadStationByPreStationId(Long preStationId) {
		if (Objects.nonNull(preStationId)) {
			throw new LineStationException("출발역이 존재하지 않습니다.");
		}
	}

	private void checkLineStationAlreadyExist(LineStation lineStation) {
		final Optional<LineStation> foundLineStation =
			findLineStationByStationId(lineStation.getStationId());

		if (foundLineStation.isPresent()) {
			throw new LineStationException("이미 존재하는 역입니다.");
		}
	}

	private Optional<LineStation> findLineStationByStationId(Long stationId) {
		return stations.stream()
		               .filter(lineStation -> stationId.equals(lineStation.getStationId()))
		               .findFirst();
	}

	private Optional<LineStation> findLineStationByPreStationId(Long preStationId) {
		return stations.stream()
		               .filter(lineStation -> preStationId.equals(lineStation.getPreStationId()))
		               .findFirst();
	}

	public void removeLineStationById(Long stationId) {
		final LineStation targetLineStation = findLineStationByStationId(stationId)
			.orElseThrow(() -> new LineStationException("삭제하려는 역이 존재하지 않습니다."));

		stations.remove(targetLineStation);
		findLineStationByPreStationId(stationId)
			.ifPresent(value -> value.updatePreLineStation(targetLineStation.getPreStationId()));
	}

	public List<Long> getLineStationsId() {
		return stations.stream()
		               .map(LineStation::getStationId)
		               .collect(toList());
	}
}
