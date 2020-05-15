package wooteco.subway.admin.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import wooteco.subway.admin.domain.Station;

public class StationCreateRequest {
	@NotNull(message = "null값이 들어가면 안됩니다.")
	@NotBlank(message = "역 명은 반드시 입력해주세요.")
	private String name;

	public Station toStation() {
		return new Station(name);
	}

	public String getName() {
		return name;
	}
}
