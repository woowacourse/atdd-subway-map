package wooteco.subway.admin.dto;

import javax.validation.constraints.NotNull;

import wooteco.subway.admin.domain.Station;

public class StationCreateRequest {
	@NotNull
	private String name;

	public Station toStation() {
		return new Station(name);
	}

	public String getName() {
		return name;
	}
}
