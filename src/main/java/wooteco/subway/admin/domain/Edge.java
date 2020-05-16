package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;

public class Edge {
	public static final int ID_MINIMUM_VALUE = 1;
	private static final int DISTANCE_MINIMUM_VALUE = 0;
	private static final int DURATION_MINIMUM_VALUE = 0;

	@Id
	private Long id;
	private long stationId;
	private long preStationId;
	private int distance;
	private int duration;

	private Edge() {
	}

	public Edge(long preStationId, long stationId, int distance, int duration) {
		validate(preStationId, stationId, distance, duration);

		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	public static Edge starter(long stationId) {
		return new Edge(stationId, stationId, DISTANCE_MINIMUM_VALUE,
			DURATION_MINIMUM_VALUE);
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

	public void updatePreStationId(long preStationId) {
		this.preStationId = preStationId;
	}

	public boolean equalsStationId(long stationId) {
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
