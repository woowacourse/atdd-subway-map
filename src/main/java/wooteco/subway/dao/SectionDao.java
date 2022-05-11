package wooteco.subway.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public interface SectionDao {

    void save(Section section);

    List<Section> findByLineId(Long lindId);

    void update(Section section);
}
