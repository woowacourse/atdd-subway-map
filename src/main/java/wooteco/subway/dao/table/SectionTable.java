package wooteco.subway.dao.table;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class SectionTable {

	private final Long id;
	private final Long lineId;
	private final Long upStationId;
	private final Long downStationId;
	private final int distance;

	public SectionTable(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
		this.id = id;
		this.lineId = lineId;
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
	}

	public SectionTable(Long id, Long upStationId, Long downStationId, int distance) {
		this(id, 0L, upStationId, downStationId, distance);
	}

	public static SectionTable from(Section section) {
		return new SectionTable(
			section.getId(),
			section.getUpStationId(),
			section.getDownStationId(),
			section.getDistance()
		);
	}

	public static SectionTable of(Long lineId, Section section) {
		return new SectionTable(
			section.getId(),
			lineId,
			section.getUpStationId(),
			section.getDownStationId(),
			section.getDistance()
		);
	}

	public Section toEntity(Station upStation, Station downStation) {
		return new Section(id, upStation, downStation, distance);
	}

	public Long getId() {
		return id;
	}

	public Long getLineId() {
		return lineId;
	}

	public Long getUpStationId() {
		return upStationId;
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public int getDistance() {
		return distance;
	}
}
