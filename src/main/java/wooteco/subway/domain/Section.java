package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private final Long id;
    private final Long lineId;
    private final SectionEdge edge;

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.edge = new SectionEdge(upStationId, downStationId, distance);
    }

    public Section(Long id, Long lineId, SectionEdge edge) {
        this(id, lineId, edge.getUpStationId(), edge.getDownStationId(), edge.getDistance());
    }

    public Section split(Section section) {
        return new Section(id, lineId, edge.splitBy(section.getEdge()));
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return edge.getUpStationId();
    }

    public Long getDownStationId() {
        return edge.getDownStationId();
    }

    public SectionEdge getEdge() {
        return edge;
    }

    public Long getId() {
        return id;
    }

    public int getDistance() {
        return edge.getDistance();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(lineId, section.lineId)
            && Objects.equals(edge, section.edge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, edge);
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", lineId=" + lineId +
            ", edge=" + edge +
            '}';
    }
}
