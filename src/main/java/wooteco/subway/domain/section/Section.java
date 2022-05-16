package wooteco.subway.domain.section;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
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

    private boolean isOnSameLine(Section section) {
        return lineId.equals(section.getLineId());
    }

    private boolean hasCommonStationWith(Section newSection) {
        return !Collections.disjoint(List.of(upStationId, downStationId),
                List.of(newSection.getUpStationId(), newSection.getDownStationId()));
    }

    public boolean isConnectedTo(Section newSection) {
        return isOnSameLine(newSection) && hasCommonStationWith(newSection);
    }

    public boolean isOverLappedWith(Section newSection) {
        return isOnSameLine(newSection)
                && (upStationId.equals(newSection.getUpStationId()) || downStationId.equals(newSection.getDownStationId()));
    }

    public boolean hasStation(Long stationId) {
        return List.of(upStationId, downStationId).contains(stationId);
    }

    public boolean hasSameValue(Section section) {
        return distance == section.distance
                && Objects.equals(upStationId, section.upStationId)
                && Objects.equals(downStationId, section.downStationId)
                && Objects.equals(id, section.id);
    }
}
