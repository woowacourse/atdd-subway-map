package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Section save(Section section);

    void deleteById(Long id);

    Optional<Section> findById(Long id);

    List<Section> findSectionStationsByLineId(Long lineId);

    void deleteAllByLineId(Long lineId);

    void batchUpdate(List<Section> sections);
}
