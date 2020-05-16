package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import wooteco.subway.admin.exception.AlreadyExistDataException;
import wooteco.subway.admin.exception.InvalidDataException;
import wooteco.subway.admin.exception.NotExistDataException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Line {
    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private String bgColor;
	private Set<LineStation> stations;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Line() {
	}

	public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
		this.name = validateName(name);
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.bgColor = bgColor;
		this.stations = new LinkedHashSet<>();
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
		this(null, name, startTime, endTime, intervalTime, bgColor);
	}

	private static String validateName(String name) {
		return Optional.ofNullable(name)
				.map(String::trim)
				.filter(it -> !it.isEmpty())
				.orElseThrow(() -> new InvalidDataException("노선 이름은 반드시 있어야 합니다!"));
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

	public Set<LineStation> getLineStations() {
		return stations;
	}

	public String getBgColor() {
		return bgColor;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void update(Line line) {
        if (line.getName() != null) {
            this.name = line.getName();
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

	public void addLineStation(LineStation newLineStation) {
		validatePreStationExist(newLineStation);
		validateDuplicatedStationExist(newLineStation);
		if (newLineStation.isDepartureLineStation()) {
			addNewDepartureLineStation(newLineStation);
			return;
		}
		addNewLineStationInMiddle(newLineStation);
	}

	private void validatePreStationExist(LineStation newLineStation) {
		if (newLineStation.getPreStationId() != null) {
			this.stations.stream()
					.map(LineStation::getStationId)
					.filter(id -> Objects.equals(id, newLineStation.getPreStationId()))
					.findFirst()
					.orElseThrow(() -> new NotExistDataException("이전역이 존재하지 않습니다!"));
		}
	}

	private void validateDuplicatedStationExist(LineStation newLineStation) {
		this.stations.stream()
				.map(LineStation::getStationId)
				.filter(id -> Objects.equals(id, newLineStation.getStationId()))
				.findFirst()
				.ifPresent(station -> {
					throw new AlreadyExistDataException("이미 존재하는 역입니다!");
				});
	}

	private int findMiddleIndex(Long preStationId) {
		List<LineStation> lineStations = new ArrayList<>(this.stations);
		return IntStream.range(0, this.stations.size())
				.filter(idx -> lineStations.get(idx).isEqualPreStationId(preStationId))
				.findFirst()
				.orElse(this.stations.size());
	}

	private void addNewLineStationInMiddle(LineStation newLineStation) {
		int middleIndex = findMiddleIndex(newLineStation.getPreStationId());
		List<LineStation> lineStations = new ArrayList<>(this.stations);
		if (middleIndex < this.stations.size()) {
			lineStations.get(middleIndex).updatePreLineStation(newLineStation.getStationId());
		}
		int totalSize = this.stations.size() + 1;

		this.stations = new LinkedHashSet<>();
		for (int i = 0; i < totalSize; i++) {
			if (i == middleIndex) {
				this.stations.add(newLineStation);
				continue;
			}
			this.stations.add(lineStations.remove(0));
		}
	}

	private void addNewDepartureLineStation(LineStation newLineStation) {
		boolean hasAnyLineStations = !this.stations.isEmpty();
		if (hasAnyLineStations) {
			LineStation previousDepartureLineStation = findDepartureLineStation()
					.orElseThrow(NotExistDataException::new);
			previousDepartureLineStation.updatePreLineStation(newLineStation.getStationId());
		}

		addLineStationAtFirst(newLineStation);
	}

	private void addLineStationAtFirst(LineStation newLineStation) {
		Set<LineStation> savedLineStations = this.stations;
		this.stations = new LinkedHashSet<>();
		this.stations.add(newLineStation);
		this.stations.addAll(savedLineStations);
	}

	public Optional<LineStation> findDepartureLineStation() {
		return this.stations.stream().
				filter(LineStation::isDepartureLineStation).
				findFirst();
	}

	public void removeLineStationById(Long stationId) {
		LineStation lineStationToRemove = this.stations.stream()
				.filter(station -> Objects.equals(station.getStationId(), stationId))
				.findFirst()
				.orElseThrow(RuntimeException::new);

		this.stations.stream()
				.filter(station -> Objects.equals(station.getPreStationId(), stationId))
				.findFirst()
				.ifPresent(station -> station.updatePreLineStation(lineStationToRemove.getPreStationId()));

		this.stations.remove(lineStationToRemove);
	}

	public List<Long> getLineStationsId() {
		return stations.stream()
				.map(LineStation::getStationId)
				.collect(Collectors.toList());
	}

	public LineStationFinder createLineStationFinder() {
		return new LineStationFinder(this.stations);
	}

	public boolean isNotEqualName(Line another) {
		return !Objects.equals(this.name, another.name);
	}
}
