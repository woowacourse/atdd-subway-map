package wooteco.subway.admin.dto;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import wooteco.subway.admin.domain.Station;

public class StationResponse {
	private Long id;
	private String name;
	private LocalDateTime createdAt;

	public StationResponse() {
	}

	public StationResponse(Long id, String name, LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.createdAt = createdAt;
	}

	public static StationResponse of(Station station) {
		return new StationResponse(station.getId(), station.getName(), station.getCreatedAt());
	}

	public static List<StationResponse> ofList(List<Station> stations) {
		return stations.stream()
			.map(StationResponse::of)
			.collect(collectingAndThen(toList(), Collections::unmodifiableList));
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
