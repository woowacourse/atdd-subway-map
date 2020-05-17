package wooteco.subway.admin.station.service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import wooteco.subway.admin.station.domain.Station;

public class StationCreateRequest {

	private static final String STATION_NAME_PATTERN = "[^\\s\\d]*";

	@NotBlank(message = "역 이름이 입력되지 않았습니다.")
	@Pattern(regexp = STATION_NAME_PATTERN, message = "역 이름에는 숫자 또는 공백이 들어갈 수 없습니다.")
	private String name;

	public StationCreateRequest() {
	}

	public String getName() {
		return name;
	}

	public Station toStation() {
		return new Station(name);
	}

}
