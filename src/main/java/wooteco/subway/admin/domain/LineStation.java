package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table
public class LineStation {
	@Column("pre_station")
	private final Long preStationId;
	@Column("station")
	private final Long stationId;
	private final int distance;
	private final int duration;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	LineStation(Long preStationId, Long stationId, int distance, int duration, LocalDateTime createdAt,
		LocalDateTime updatedAt) {
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static LineStation of(Long preStationId, Long stationId, int distance, int duration) {
		LocalDateTime currentTime = LocalDateTime.now();
		return new LineStation(preStationId, stationId, distance, duration, currentTime, currentTime);
	}

	public LineStation updatePreLineStation(Long preStationId) {
		return new LineStation(preStationId, this.stationId, this.distance, this.duration, this.createdAt,
			LocalDateTime.now());
	}

	public boolean isStartStation() {
		return preStationId == null;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final LineStation that = (LineStation)o;
		return Objects.equals(preStationId, that.preStationId) &&
			stationId.equals(that.stationId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(preStationId, stationId);
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
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
}
