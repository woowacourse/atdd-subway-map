package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;

public class Edge {
	public static final int ID_MINIMUM_VALUE = 1;
	private static final int DISTANCE_MINIMUM_VALUE = 0;
	private static final int DURATION_MINIMUM_VALUE = 0;

	@Id
	private Long id;
	private final long stationId;
	private final long preStationId;
	private final int distance;
	private final int duration;

	Edge(final Long id, final long preStationId, final long stationId, final int distance,
		final int duration) {
		validate(preStationId, stationId, distance, duration);

		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	public static Edge of(final long preStationId, final long stationId,
		final int distance, final int duration) {
		return new Edge(null, preStationId, stationId, distance, duration);
	}

	public static Edge starter(long stationId) {
		return new Edge(null, stationId, stationId, DISTANCE_MINIMUM_VALUE,
			DURATION_MINIMUM_VALUE);
	}

	public Edge withId(final Long id) {
		return new Edge(id, stationId, preStationId, distance, duration);
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
		return new Edge(this.id, preStationId, this.stationId, this.distance,
			this.duration);
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
