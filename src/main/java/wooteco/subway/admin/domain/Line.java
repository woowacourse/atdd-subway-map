package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;

import javax.swing.text.html.Option;

public class Line {
	@Id
	private Long id;
	private String title;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private Set<LineStation> stations;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String bgColor;

	public Line() {
	}

	public Line(Long id, String title, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		this.bgColor = bgColor;
		stations = new HashSet<>();
	}

	public Line(String title, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
		this(null, title, startTime, endTime, intervalTime, bgColor);
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
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

	public Set<LineStation> getStations() {
		return stations;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public String getBgColor() {
		return bgColor;
	}

	public void update(Line line) {
		if (line.getTitle() != null) {
			this.title = line.getTitle();
		}
		if (line.getStartTime() != null) {
			this.startTime = line.getStartTime();
		}
		if (line.getEndTime() != null) {
			this.endTime = line.getEndTime();
		}
		if (line.getIntervalTime() != 0) {
			this.intervalTime = line.getIntervalTime();
		}
		if (line.getBgColor() != null) {
			this.bgColor = line.getBgColor();
		}

		this.updatedAt = LocalDateTime.now();
	}

	public void addLineStation(LineStation lineStation) {
		this.stations.add(lineStation);
	}

	public void removeLineStationById(Long stationId) {
		this.stations.stream().filter(x -> x.getStationId().equals(stationId))
				.findAny().ifPresent(target -> {
			this.stations.remove(target);
		});
	}

	public List<Long> getLineStationsId() {
		List<LineStation> result = new ArrayList<>();
		result.add(stations.stream().filter(x -> x.getPreStationId() == 0).findAny()
				.orElseThrow(IllegalArgumentException::new));

		for (int i = 0; i < stations.size() - 1; i++) {
			result.add(findNextLineStation(result.get(i)));
		}

		System.out.println(result.stream()
				.map(LineStation::getStationId)
				.collect(Collectors.toList()));
		return result.stream()
				.map(LineStation::getStationId)
				.collect(Collectors.toList());
	}

	private LineStation findNextLineStation(final LineStation preStation) {
		return stations.stream().filter(x->x.getPreStationId().equals(preStation.getStationId()))
				.findAny().orElseThrow(IllegalArgumentException::new);
	}

	public void updatePreStation(final LineStation toInput) {
		stations.stream()
				.filter(x -> x.getPreStationId()
						.equals(toInput.getPreStationId()))
				.findAny().ifPresent(lineStation -> {
			lineStation.updatePreLineStation(toInput.getStationId());
		});
	}

	public void updatePreStationWhenRemove(final Long toRemoveId) {
		LineStation removeStation =
				stations.stream().filter(x-> x.getStationId().equals(toRemoveId))
				.findAny().orElseThrow(IllegalArgumentException::new);

		stations.stream()
				.filter(x -> x.getPreStationId()
						.equals(toRemoveId))
				.findAny().ifPresent(lineStation -> {
			lineStation.updatePreLineStation(removeStation.getPreStationId());
		});
	}
}
