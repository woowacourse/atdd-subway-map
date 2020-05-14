package wooteco.subway.admin.dto.controller.response;

import wooteco.subway.admin.dto.service.response.StationCreateServiceResponse;

import java.time.LocalDateTime;

public class StationCreateControllerResponse {
	private Long id;
	private String name;
	private LocalDateTime createdAt;

	private StationCreateControllerResponse(Long id, String name, LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.createdAt = createdAt;
	}

	public static StationCreateControllerResponse of(StationCreateServiceResponse response) {
		return new StationCreateControllerResponse(response.getId(), response.getName(), response.getCreatedAt());
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
