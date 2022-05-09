package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {

    long save(Section section);

    List<Section> findAll();

    void delete(Long id);

    boolean existSectionById(Long id);
}
