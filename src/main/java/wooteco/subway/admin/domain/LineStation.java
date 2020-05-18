package wooteco.subway.admin.domain;

import java.util.Objects;

import org.springframework.data.relational.core.mapping.Column;

public class LineStation {
	private static final Long PRESTATION_ID_OF_FIRST_LINESTATION = 0L;

    @Column("station")
    private Long stationId;
    @Column("pre_station")
    private Long preStationId;
    private int distance;
    private int duration;

    public LineStation() {
    }

    public LineStation(Long preStationId, Long stationId, int distance, int duration) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

	public void updatePreStationId(Long preStationId) {
		this.preStationId = preStationId;
	}

	public boolean isBefore(LineStation another) {
		return this.stationId.equals(another.preStationId);
	}

	public boolean ifAfter(LineStation another) {
		return this.preStationId.equals(another.stationId);
    }

	public boolean isFirstLineStation() {
		return preStationId.equals(PRESTATION_ID_OF_FIRST_LINESTATION);
	}

	public boolean equalsStationId(Long stationId) {
		return this.stationId.equals(stationId);
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		LineStation that = (LineStation)o;
		return distance == that.distance &&
			duration == that.duration &&
			Objects.equals(stationId, that.stationId) &&
			Objects.equals(preStationId, that.preStationId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(stationId, preStationId, distance, duration);
    }

    @Override
    public String toString() {
        return "LineStation{" +
            "stationId=" + stationId +
            ", preStationId=" + preStationId +
            ", distance=" + distance +
            ", duration=" + duration +
            '}';
    }
}
