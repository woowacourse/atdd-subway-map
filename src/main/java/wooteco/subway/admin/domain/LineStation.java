package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Column;

public class LineStation {

	@Column("index")
	private Long index;

	@Column("pre_station_id")
	private Long preStationId;

	@Column("station_id")
	private Long stationId;

	@Column("distance")
	private Integer distance;

	@Column("duration")
	private Integer duration;

	public LineStation() {
	}

	public LineStation(Long index, Long preStationId, Long stationId, Integer distance,
		Integer duration) {
		this.index = index;
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	public Long getIndex() {
		return index;
	}

	public Long getPreStationId() {
		return preStationId;
	}

	public Long getStationId() {
		return stationId;
	}

	public Integer getDistance() {
		return distance;
	}

	public Integer getDuration() {
		return duration;
	}

	public void updatePreLineStation(Long preStationId) {
		this.preStationId = preStationId;
	}

}
