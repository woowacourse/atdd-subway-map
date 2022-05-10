package wooteco.subway.domain;

public class SectionWithStation {
    private Long id;
    private long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;


    public SectionWithStation(Long id, long lineId, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static SectionWithStation of(Section section, Station upStation, Station downStation) {
        return new SectionWithStation(section.getId(), section.getLineId(), upStation, downStation, section.getDistance());
    }

    public Long getId() {
        return id;
    }

    public long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Station getUpStation() {
        return upStation;
    }
}
