package wooteco.subway.admin.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import wooteco.subway.admin.domain.LineStation;

public class LineStationCreateRequest {
	private static final int MINIMUM_INTEGER_VALUE = 1;

	private Long preStationId;

	@NotNull(message = "구간에 추가할 역을 입력해야 합니다.")
	private Long stationId;

	@Min(value = MINIMUM_INTEGER_VALUE, message = "구간 사이 거리값은 양수여야 합니다.")
	private int distance;

	@Min(value = MINIMUM_INTEGER_VALUE, message = "구간 사이 소요 시간은 양수여야 합니다.")
	private int duration;

	private LineStationCreateRequest() {
	}

	public LineStationCreateRequest(Long preStationId, Long stationId, int distance, int duration) {
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	public LineStation toLineStation() {
		return new LineStation(preStationId, stationId, distance, duration);
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
