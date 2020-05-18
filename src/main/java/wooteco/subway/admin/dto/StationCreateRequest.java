package wooteco.subway.admin.dto;

import javax.validation.constraints.NotBlank;

import wooteco.subway.admin.domain.Station;

public class StationCreateRequest {
	@NotBlank(message = "역 명은 반드시 입력해주세요.")
	private String name;

	public Station toStation() {
		return new Station(name);
	}

	public String getName() {
		return name;
	}
}
