package wooteco.subway.admin.domain;

import java.util.Objects;
import org.springframework.data.relational.core.mapping.Column;
import wooteco.subway.admin.util.EasyExceptionMaker;

public class LineStation {

	@Column("station")
	private Long stationId;
	@Column("pre_station")
	private Long preStationId;
	private int distance;
	private int duration;

	public LineStation() {
	}

	public LineStation(Long preStationId, Long stationId, int distance, int duration) {
		validate(preStationId, stationId, distance, duration);
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	private void validate(Long preStationId, Long stationId, int distance, int duration) {
		Objects.requireNonNull(stationId, "대상역이 존재하지 않습니다.");
		EasyExceptionMaker.validateThrowIAE(preStationId != null && preStationId.equals(stationId),
			"이전역과 대상역이 같을 수 없습니다.");
		EasyExceptionMaker.validateThrowIAE(distance <= 0, "거리는 0보다 커야합니다.");
		EasyExceptionMaker.validateThrowIAE(duration <= 0, "시간은 0보다 커야합니다.");
	}

	public boolean isPreStationBy(LineStation other) {
		return this.stationId.equals(other.preStationId);
	}

	public void updatePreStationId(Long preStationId) {
		this.preStationId = preStationId;
	}

	public boolean isSameStationId(Long stationId) {
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
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		LineStation that = (LineStation) o;
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
