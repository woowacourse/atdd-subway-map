package wooteco.util;

import wooteco.subway.domain.line.section.Section;
import wooteco.subway.domain.line.value.line.LineId;
import wooteco.subway.domain.line.value.section.Distance;
import wooteco.subway.domain.line.value.section.SectionId;
import wooteco.subway.domain.station.value.StationId;

public class SectionFactory {

    public static Section create(Long id, Long lineId, Long upStationId, Long downStationId, Long distance) {
        return new Section(
                id == null ? SectionId.empty() : new SectionId(id),
                new LineId(lineId),
                new StationId(upStationId),
                new StationId(downStationId),
                new Distance(distance)
        );
    }

    public static Section create(Long lineId, Long upStationId, Long downStationId, Long distance) {
        return create(null, lineId, upStationId, downStationId, distance);
    }

}
