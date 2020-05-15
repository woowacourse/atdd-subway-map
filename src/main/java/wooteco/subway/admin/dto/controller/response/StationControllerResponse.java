package wooteco.subway.admin.dto.controller.response;

import wooteco.subway.admin.dto.service.response.StationServiceResponse;

import java.time.LocalDateTime;

public class StationControllerResponse {
	private Long id;
	private String name;
	private LocalDateTime createdAt;

	private StationControllerResponse(Long id, String name, LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.createdAt = createdAt;
	}

	public static StationControllerResponse of(StationServiceResponse response) {
		return new StationControllerResponse(response.getId(), response.getName(), response.getCreatedAt());
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
