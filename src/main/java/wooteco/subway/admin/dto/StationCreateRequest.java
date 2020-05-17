package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Station;

public class StationCreateRequest {
	private String name;

	public Station toStation() {
		return Station.of(name);
	}

	public String getName() {
		return name;
	}
}
