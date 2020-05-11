package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Station;

public class StationRequest {
	private String name;

	public StationRequest() {
	}

	public Station toStation() {
		return new Station(name);
	}

	public String getName() {
		return name;
	}
}
