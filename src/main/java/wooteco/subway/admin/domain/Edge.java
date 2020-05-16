package wooteco.subway.admin.domain;

import java.time.LocalDateTime;

public class Edge {
	public static final int ID_MINIMUM_VALUE = 1;
	private static final int DISTANCE_MINIMUM_VALUE = 0;
	private static final int DURATION_MINIMUM_VALUE = 0;

	private final long stationId;
	private final long preStationId;
	private final int distance;
	private final int duration;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	Edge(final long preStationId, final long stationId, final int distance,
		final int duration, LocalDateTime createdAt, LocalDateTime updatedAt) {
		validate(preStationId, stationId, distance, duration);

		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static Edge of(final long preStationId, final long stationId,
		final int distance, final int duration) {
		LocalDateTime currentTime = LocalDateTime.now();

		return new Edge(preStationId, stationId, distance, duration, currentTime,
			currentTime);
	}

	public static Edge starter(long stationId) {
		LocalDateTime currentTime = LocalDateTime.now();

		return new Edge(stationId, stationId, DISTANCE_MINIMUM_VALUE,
			DURATION_MINIMUM_VALUE, currentTime, currentTime);
	}

	private void validate(long preStationId, long stationId, int distance, int duration) {
		if (preStationId < ID_MINIMUM_VALUE) {
			throw new IllegalArgumentException(
				"적절하지 않은 preStationId 값입니다.: " + preStationId);
		}
		if (stationId < ID_MINIMUM_VALUE) {
			throw new IllegalArgumentException("적절하지 않은 stationId 값입니다.: " + stationId);
		}
		if (distance < DISTANCE_MINIMUM_VALUE) {
			throw new IllegalArgumentException("적절하지 않은 distance 값입니다.: " + distance);
		}
		if (duration < DURATION_MINIMUM_VALUE) {
			throw new IllegalArgumentException("적절하지 않은 duration 값입니다.: " + duration);
		}
	}

	public Edge updatePreStationId(final long preStationId) {
		return new Edge(preStationId, this.stationId, this.distance, this.duration,
			createdAt, LocalDateTime.now());
	}

	public boolean equalsStationId(final long stationId) {
		return this.stationId == stationId;
	}

	public boolean isNotStartEdge() {
		return !isStartEdge();
	}

	public boolean isStartEdge() {
		return preStationId == stationId;
	}

	public long getStationId() {
		return stationId;
	}

	public long getPreStationId() {
		return preStationId;
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

	@Override
	public String toString() {
		return "Edge{" +
			"stationId=" + stationId +
			", preStationId=" + preStationId +
			", distance=" + distance +
			", duration=" + duration +
			'}';
	}
}
