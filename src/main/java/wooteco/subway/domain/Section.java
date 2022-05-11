package wooteco.subway.domain;

import java.util.Objects;

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

    public boolean isSameDownStationId(Long downStationId) {
        return this.downStation.isSameId(downStationId);
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void setUpStationId(Long upStationId) {
        this.upStation = new Station(upStationId);
    }

    public void setDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void setDownStationId(Long downStationId) {
        this.downStation = new Station(downStationId);
    }


    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance) && Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance, line);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                ", line=" + line +
                '}';
    }
}
