package wooteco.subway.admin.dto.controller.request;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.view.request.StationCreateViewRequest;

public class StationCreateControllerRequest {
	private String name;

	private StationCreateControllerRequest(String name) {
		this.name = name;
	}

	public static StationCreateControllerRequest of(StationCreateViewRequest request) {
		return new StationCreateControllerRequest(request.getName());
	}

	public String getName() {
		return name;
	}

	public Station toStation() {
		return new Station(name);
	}
}
