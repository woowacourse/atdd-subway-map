package wooteco.subway.domain;

import java.util.List;
import java.util.Objects;

public class Section {

    private Long id;
    private final Long lindId;
    private final Long upStationId;
    private final Long downStationId;
    private final Integer distance;

    public Section(Long id, Long lindId, Long upStationId, Long downStationId, Integer distance) {
        this.id = id;
        this.lindId = lindId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(final Long lindId, final Long upStationId, final Long downStationId, final Integer distance) {
        this(null, lindId, upStationId, downStationId, distance);
    }

    public Long getId() {
        return id;
    }

    public Long getLindId() {
        return lindId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public boolean hasHigherDistance(Section another) {
        return this.distance - another.distance >= 0;
    }

    public boolean hasSameUpStationOf(Section another) {
        return hasSameUpStation(another) || another.upStationId.equals(this.downStationId);
    }

    public boolean hasSameDownStationOf(Section another) {
        return another.downStationId.equals(this.upStationId) || hasSameDownStation(another);
    }

    public boolean hasSameUpStation(Section another) {
        return upStationId.equals(another.upStationId);
    }

    public boolean hasSameDownStation(Section another) {
        return downStationId.equals(another.downStationId);
    }

    public Section changeSection(Section another) {
        if (upStationId.equals(another.upStationId)) {
            return new Section(id, lindId, another.downStationId, this.downStationId, distance - another.distance);
        }
        return new Section(id, lindId, upStationId, another.upStationId, distance - another.distance);
    }

    public boolean hasNoId() {
        return id == null;
    }

    public boolean isUpSection(List<Section> section) {
        return section.stream()
                .filter(it -> it.hasSameUpStationOf(this))
                .count() == 1;
    }

    public Section findNextSection(List<Section> sections) {
        return sections.stream()
                .filter(another -> this.downStationId.equals(another.upStationId))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Section section = (Section) o;
        return Objects.equals(lindId, section.lindId) && Objects
                .equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId)
                && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lindId, upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", lindId=" + lindId +
                ", upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
