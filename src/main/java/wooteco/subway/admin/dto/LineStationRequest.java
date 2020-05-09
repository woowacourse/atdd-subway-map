package wooteco.subway.admin.dto;

public class LineStationRequest {
	private String lineName;
	private String preStationName;
	private String stationName;
	private int distance;
	private int duration;

	public LineStationRequest() {
	}

	public LineStationRequest(String lineName, String preStationName, String stationName,
			int distance, int duration) {
		this.lineName = lineName;
		this.preStationName = preStationName;
		this.stationName = stationName;
		this.distance = distance;
		this.duration = duration;
	}

	public String getLineName() {
		return lineName;
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
