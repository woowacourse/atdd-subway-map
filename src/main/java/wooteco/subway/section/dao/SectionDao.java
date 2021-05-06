package wooteco.subway.section.dao;

import org.springframework.stereotype.Repository;
import wooteco.subway.section.domain.Section;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionDao {
    void save(Section section);

//    Optional<Section> findById(Long id);
//
//    Optional<Section> findByName(String sectionName);

    List<Section> findAllByLineId(Long lineId);

    void delete(Long id);

    void update(Section newSection);

    void updateByDownStationId(Long lineId, Long downStationId, Long upStationId);

    void updateByUpStationId(Long lineId, Long upStationId, Long downStationId);
}
