package wooteco.subway.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;


@Getter
@Setter
@ToString
public class Section {
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;
    private Long lineId;

    public Section(Long upStationId, Long downStationId, Integer distance) {
        this(null, upStationId, downStationId, distance, null);
    }

    public Section(Long upStationId, Long downStationId, Integer distance, Long lineId) {
        this(null, upStationId, downStationId, distance, lineId);
    }

    public Section(Long id, Long upStationId, Long downStationId, Integer distance, Long lineId) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.lineId = lineId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId) && Objects.equals(distance, section.distance) && Objects.equals(lineId, section.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStationId, downStationId, distance, lineId);
    }
}
