package wooteco.subway.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {

    public Section save(final Section section) {
        return null;
    }

    public List<Section> findSectionsByLineId(final Long lineId) {
        return null;
    }
}
