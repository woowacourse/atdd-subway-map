package wooteco.subway.section.dao;

import org.springframework.stereotype.Repository;
import wooteco.subway.section.domain.Section;

import java.util.List;

@Repository
public interface SectionDao {
    void save(Section section);

    List<Section> findAllByLineId(Long lineId);

    void delete(Long id);

    void update(Section section);
}
