package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Section save(Section section);

    void saveAll(List<Section> sections);

    List<Section> findByLineId(Long id);

    List<Section> findAll();

    int deleteById(Long id);

    int deleteByLineId(Long lineId);
}
