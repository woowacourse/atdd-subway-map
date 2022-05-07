package wooteco.subway.domain;

import java.util.List;

public class Section {

	private static final long TEMPORARY_ID = 0L;

	private final Long id;
	private final Station upStation;
	private final Station downStation;
	private final int distance;

	public Section(Station upStation, Station downStation, int distance) {
		this(TEMPORARY_ID, upStation, downStation, distance);
	}

	public Section(Long id, Station upStation, Station downStation, int distance) {
		validateDistance(distance);
		this.id = id;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	private void validateDistance(int distance) {
		if (distance < 1) {
			throw new IllegalArgumentException("거리는 1 이상이어야 합니다.");
		}
	}

	public List<Station> getStations() {
		return List.of(upStation, downStation);
	}

	public Long getId() {
		return id;
	}

	public Long getUpStationId() {
		return upStation.getId();
	}

	public Long getDownStationId() {
		return downStation.getId();
	}

	public int getDistance() {
		return distance;
	}
}
