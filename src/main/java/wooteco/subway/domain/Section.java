package wooteco.subway.domain;

import java.util.Objects;

public class Section {

	private static final long TEMPORARY_ID = 0L;
	private static final int MINIMUM_DISTANCE = 0;

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

	public boolean isUpStation(Station station) {
		return this.upStation.equals(station);
	}

	public boolean isDownStation(Station station) {
		return this.downStation.equals(station);
	}

	public boolean hasAnySameStation(Section section) {
		Station upStation = section.upStation;
		Station downStation = section.downStation;
		return hasSameUpStation(section) || this.upStation.equals(downStation)
			|| this.downStation.equals(upStation) || hasSameDownStation(section);
	}

	public Section dividedBy(Section section) {
		if (hasSameUpStation(section)) {
			return new Section(id, section.downStation, downStation, subtractDistance(section));
		}
		if (hasSameDownStation(section)) {
			return new Section(id, upStation, section.upStation, subtractDistance(section));
		}
		throw new IllegalArgumentException("상행역이나 하행역 중 하나가 같아야 구간을 나눌 수 있습니다.");
	}

	public boolean hasSameUpStation(Section section) {
		return this.upStation.equals(section.upStation);
	}

	public boolean hasSameDownStation(Section section) {
		return downStation.equals(section.downStation);
	}

	private int subtractDistance(Section section) {
		int result = this.distance - section.distance;
		if (result <= MINIMUM_DISTANCE) {
			throw new IllegalArgumentException("기존 구간의 거리가 더 길어야 합니다.");
		}
		return result;
	}

	public Long getId() {
		return id;
	}

	public Station getUpStation() {
		return upStation;
	}

	public Station getDownStation() {
		return downStation;
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Section section = (Section)o;
		return getDistance() == section.getDistance() && Objects.equals(getId(), section.getId())
			&& Objects.equals(getUpStation(), section.getUpStation()) && Objects.equals(
			getDownStation(), section.getDownStation());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getUpStation(), getDownStation(), getDistance());
	}

	@Override
	public String toString() {
		return "Section{" +
			"id=" + id +
			", upStation=" + upStation +
			", downStation=" + downStation +
			", distance=" + distance +
			'}';
	}
}
