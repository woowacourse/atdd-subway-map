package wooteco.subway.admin.domain;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import wooteco.subway.admin.controller.exception.InvalidLineFieldException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Line {
	@Id
	private Long id;
	private String name;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private String lineColor;
	@Embedded.Empty
	private LineStations stations;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Line() {
	}

	public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String lineColor) {
		validate(name, lineColor);

		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.lineColor = lineColor;
		stations = LineStations.of(new ArrayList<>());
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String lineColor) {
		this(null, name, startTime, endTime, intervalTime, lineColor);
	}

	private void validate(String name, String lineColor) {
		if (StringUtils.isBlank(name)) {
			throw new InvalidLineFieldException("노선 이름이 입력되지 않았습니다.");
		}

		if (StringUtils.isBlank(lineColor)) {
			throw new InvalidLineFieldException("노선 색상이 입력되지 않았습니다.");
		}
	}

	public void addLineStationOnFirst(LineStation inputLineStation) {
		stations.addLineStationOnFirst(inputLineStation);
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
		if (line.getLineColor() != null) {
			this.lineColor = line.getLineColor();
		}

		this.updatedAt = LocalDateTime.now();
	}

	public void addLineStation(LineStation inputLineStation) {
		stations.addLineStation(inputLineStation);
	}


	public void addLineStationOnLast(LineStation lineStation) {
		stations.addLineStationOnLast(lineStation);
	}

	public void removeLineStationById(Long stationId) {
		stations.removeLineStationById(stationId);
	}


	public List<Long> getLineStationsId() {
		return stations.getLineStationsId();
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

	public String getLineColor() {
		return lineColor;
	}

	public List<LineStation> getStations() {
		return stations.getLineStations();
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Line line = (Line) o;
		return Objects.equals(id, line.id) &&
				Objects.equals(name, line.name) &&
				Objects.equals(lineColor, line.lineColor);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, lineColor);
	}
}
