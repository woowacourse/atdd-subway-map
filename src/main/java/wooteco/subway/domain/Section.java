package wooteco.subway.domain;

public class Section {
    private Long id;
    private Station upStation;
    private Station downStation;
    private Integer distance;
    private Line line;

    public Section(Station upStation, Station downStation, Integer distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this(null, new Station(upStationId), new Station(downStationId), distance, new Line(lineId));
    }

    public Section(Long id, Station upStation, Station downStation, int distance, Line line) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public static class SectionBuilder {

        private Long id;
        private Station upStation;
        private Station downStation;
        private Integer distance;
        private Line line;

        public SectionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SectionBuilder upStation(Station upStation) {
            this.upStation = upStation;
            return this;
        }

        public SectionBuilder upStationId(Long upStationId) {
            this.upStation = new Station(upStationId);
            return this;
        }

        public SectionBuilder downStation(Station downStation) {
            this.downStation = downStation;
            return this;
        }

        public SectionBuilder downStationId(Long downStationId) {
            this.downStation = new Station(downStationId);
            return this;
        }

        public SectionBuilder distance(Integer distance) {
            this.distance = distance;
            return this;
        }

        public SectionBuilder line(Line line) {
            this.line = line;
            return this;
        }

        public SectionBuilder lineId(Long lineId) {
            this.line = new Line(lineId);
            return this;
        }

        public Section build() {
            return new Section(id, upStation, downStation, distance, line);
        }
    }

    public static SectionBuilder builder() {
        return new SectionBuilder();
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

    public int getDistance() {
        return distance;
    }

    public Line getLine() {
        return line;
    }
}
