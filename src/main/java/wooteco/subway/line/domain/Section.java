package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

public class Section {
    private Long id;
    private Line line;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(){}

    public Section(final Line line, final Station upStation, final Station downStation, final int distance) {
        this(0L, line, upStation, downStation, distance);
    }

    public Section(final Long id, final Line line, final Station upStation, final Station downStation, final int distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long id() {
        return id;
    }

    public Line line() {
        return line;
    }

    public void changeLine(Line line) {
        this.line = line;
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

    /*    public Section(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this(0L, lineId, upStationId, downStationId, distance);
    }

    public Section(final Long id, final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long id() {
        return id;
    }

    public Long lineId() {
        return lineId;
    }

    public Long upsStationId() {
        return upStationId;
    }

    public Long downStationId() {
        return downStationId;
    }

    public int distance() {
        return distance;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public void setUpStationId(Long upStationId) {
        this.upStationId = upStationId;
    }

    public void setDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    } */
}
