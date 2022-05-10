package wooteco.subway.domain.repository;

import wooteco.subway.domain.Section;

import java.util.List;

public interface SectionRepository {
    Section save(Section section);

    List<Section> findAllByLineId(Long lineId);

    boolean existsByUpStationIdWithLineId(Long upStationId, Long lineId);

    void deleteById(Long id);

    boolean existsByDownStationIdWithLineId(Long downStationId, Long lineId);
}
