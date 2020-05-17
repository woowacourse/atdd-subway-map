package wooteco.subway.admin.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class LineStationCreateByNameRequest {
	private String preStationName;
	@NotBlank(message = "대상역은 꼭 입력해야합니다.")
	private String stationName;
	@Min(1)
	private int distance;
	@Min(1)
	private int duration;

	private LineStationCreateByNameRequest() {
	}

	public LineStationCreateByNameRequest(String preStationName, String stationName, int distance, int duration) {
		this.preStationName = preStationName;
		this.stationName = stationName;
		this.distance = distance;
		this.duration = duration;
	}

	public String getPreStationName() {
		return preStationName;
	}

	public String getStationName() {
		return stationName;
	}

	public int getDistance() {
		return distance;
	}

	public int getDuration() {
		return duration;
	}
}
