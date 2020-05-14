package wooteco.subway.admin.dto.controller.response;

import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.service.response.LineCreateServiceResponse;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class LineCreateControllerResponse {
	private Long id;
	private String name;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private String lineColor;
	private List<LineStation> stations;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private LineCreateControllerResponse(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime,
										 String lineColor, List<LineStation> stations, LocalDateTime createdAt,
										 LocalDateTime updatedAt) {
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.lineColor = lineColor;
		this.stations = stations;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static LineCreateControllerResponse of(LineCreateServiceResponse response) {
		return new LineCreateControllerResponse(response.getId(), response.getName(), response.getStartTime(),
				response.getEndTime(), response.getIntervalTime(), response.getLineColor(), response.getStations(),
				response.getCreatedAt(), response.getUpdatedAt());
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

	public List<LineStation> getStations() {
		return stations;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
