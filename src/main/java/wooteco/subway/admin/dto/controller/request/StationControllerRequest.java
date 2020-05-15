package wooteco.subway.admin.dto.controller.request;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.view.request.StationViewRequest;

public class StationControllerRequest {
	private String name;

	private StationControllerRequest(String name) {
		this.name = name;
	}

	public static StationControllerRequest of(StationViewRequest request) {
		return new StationControllerRequest(request.getName());
	}

	public String getName() {
		return name;
	}

	public Station toStation() {
		return new Station(name);
	}
}
