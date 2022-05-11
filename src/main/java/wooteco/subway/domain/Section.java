package wooteco.subway.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class Section {
    private Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section revisedBy(Section addedSection) {
        int revisedDistance = distance - addedSection.getDistance();

        if (Objects.equals(upStationId, addedSection.getUpStationId())) {
            return new Section(id, lineId, addedSection.getDownStationId(), downStationId, revisedDistance);
        }

        return new Section(id, lineId, upStationId, addedSection.getUpStationId(), revisedDistance);
    }

    public boolean isLongerThan(Section section) {
        return distance >= section.getDistance();
    }
}
