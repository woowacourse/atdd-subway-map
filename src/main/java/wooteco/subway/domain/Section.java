package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.domain.exception.UnmergeableException;
import wooteco.subway.domain.exception.UnsplittableException;

public class Section {

    private final Long id;
    private final Long lineId;
    private final SectionEdge edge;

    public Section(Long lineId, SectionEdge edge) {
        this(null, lineId, edge);
    }

    public Section(Long id, Long lineId, SectionEdge edge) {
        this.id = id;
        this.lineId = lineId;
        this.edge = edge;
    }

    public Section split(Section section) {
        if (!isSameLineId(section.getLineId())) {
            throw new UnsplittableException(this, section);
        }
        return new Section(lineId, edge.split(section.edge));
    }

    private boolean isSameLineId(Long lineId) {
        return this.lineId.equals(lineId);
    }

    public Section merge(Section section) {
        if (!isSameLineId(section.lineId)) {
            throw new UnmergeableException(this, section);
        }
        return new Section(lineId, edge.merge(section.edge));
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
