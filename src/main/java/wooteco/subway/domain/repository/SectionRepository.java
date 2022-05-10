package wooteco.subway.domain.repository;

import wooteco.subway.domain.Section;

import java.util.List;
import java.util.Optional;

public interface SectionRepository {
    Section save(Section section);

    List<Section> findAllByLineId(Long lineId);

    Optional<Section> findByUpStationIdWithLineId(Long upStationId, Long lineId);
}
