package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Section insert(Section section);

    List<Section> findByLineId(Long lineId);

    List<Section> save(Long lineId, List<Section> sections);

    void deleteByLineId(Long lineId);
}
