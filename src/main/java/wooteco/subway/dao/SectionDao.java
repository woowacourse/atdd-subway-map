package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {
    Long save(Section section);

    Long update(Long id, Section section);

    Section findById(Long id);

    List<Section> findAllByLineId(Long lineId);

    void deleteAllByLineId(Long id);
}
