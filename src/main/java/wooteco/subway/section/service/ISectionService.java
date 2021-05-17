package wooteco.subway.section.service;

import wooteco.subway.section.domain.Section;

public interface ISectionService {

    void addSection(final Long lineId, final Section section);

    void deleteSection(final Long lineId, final Long stationId);
}
