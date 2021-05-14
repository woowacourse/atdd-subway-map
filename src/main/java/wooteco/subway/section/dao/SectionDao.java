package wooteco.subway.section.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.domain.Section;

@Repository
public interface SectionDao {
    void save(Section section);

    List<Section> findAllByLineId(Long lineId);

    void delete(Long id);

    void update(Section section);

    List<Section> findByStationId(Long id);
}
