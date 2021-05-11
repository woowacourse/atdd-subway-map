package wooteco.util;

import wooteco.subway.domain.line.section.Section;

public class SectionFactory {

    public static Section create(Long id, Long lineId, Long upStationId, Long downStationId, Long distance) {
        return new Section(
                id,
                lineId,
                upStationId,
                downStationId,
                distance
        );
    }

    public static Section create(Long lineId, Long upStationId, Long downStationId, Long distance) {
        return create(null, lineId, upStationId, downStationId, distance);
    }

}
