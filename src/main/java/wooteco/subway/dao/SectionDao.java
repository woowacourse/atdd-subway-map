package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Section insert(Section section);

    List<Section> save(List<Section> sections);

    List<Section> findByLineId(Long lineId);

    void deleteByLineId(Long lineId);
}
