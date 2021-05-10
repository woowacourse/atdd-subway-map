package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

public class Section {
    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;


    public Section() {
    }

    public Section(Long upStationId, Long downStationId, int distance) {
        this(0L, 0L, new Station(upStationId), new Station(downStationId), distance);
    }

    public Section(final Station upStation, final Station downStation, final int distance) {
        this(0L, 0L, upStation, downStation, distance);
    }

    public Section(final Long lineId, final Station upStation, final Station downStation, final int distance) {
        this(0L, lineId, upStation, downStation, distance);
    }

    public Section(final Long id, final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this(0L, 0L, new Station(upStationId), new Station(downStationId), distance);
    }

    public Section(final Long id, final Long lineId, final Station upStation, final Station downStation, final int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long id() {
        return id;
    }

    public Station upStation() {
        return upStation;
    }

    public void changeUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public Station downStation() {
        return downStation;
    }

    public void changeDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public int distance() {
        return distance;
    }

    public void changeDistance(int distance) {
        this.distance = distance;
    }

    public Long lineId() {
        return lineId;
    }


    public boolean has(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }
}
