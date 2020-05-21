package wooteco.subway.admin.dto.controller.response;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.service.response.LineWithStationsServiceResponse;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class LineWithStationsControllerResponse {
	private Long id;
	private String name;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private String lineColor;
	private List<Station> stations;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private LineWithStationsControllerResponse(Long id, String name, LocalTime startTime, LocalTime endTime,
											   int intervalTime, String lineColor, LocalDateTime createdAt,
											   LocalDateTime updatedAt, List<Station> stations) {
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.lineColor = lineColor;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.stations = stations;
	}

	public static LineWithStationsControllerResponse of(LineWithStationsServiceResponse response) {
		return new LineWithStationsControllerResponse(response.getId(), response.getName(), response.getStartTime(),
				response.getEndTime(), response.getIntervalTime(), response.getLineColor(), response.getCreatedAt(),
				response.getUpdatedAt(), response.getStations());
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public int getIntervalTime() {
		return intervalTime;
	}

	public String getLineColor() {
		return lineColor;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public List<Station> getStations() {
		return stations;
	}
}
