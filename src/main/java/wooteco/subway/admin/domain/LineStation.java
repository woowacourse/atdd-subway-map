package wooteco.subway.admin.domain;

import java.time.LocalDateTime;

import org.springframework.data.relational.core.mapping.Column;

public class LineStation {
	@Column("station")
	private Long stationId;
	@Column("pre_station")
	private Long preStationId;
	private int distance;
	private int duration;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public LineStation() {
	}

	public LineStation(Long stationId, Long preStationId, int distance, int duration) {
		this.stationId = stationId;
		this.preStationId = preStationId;
		this.distance = distance;
		this.duration = duration;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Long getPreStationId() {
		return preStationId;
	}

	public Long getStationId() {
		return stationId;
	}

	public int getDistance() {
		return distance;
	}

	public int getDuration() {
		return duration;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void updatePreLineStation(Long preStationId) {
		this.preStationId = preStationId;
	}

}
