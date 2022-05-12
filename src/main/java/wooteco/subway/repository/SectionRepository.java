package wooteco.subway.repository;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.CommonLineDao;
import wooteco.subway.dao.CommonSectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.SectionRequest;

@Repository
public class SectionRepository {

    private final CommonSectionDao sectionDao;
    private final CommonLineDao lineDao;

    public SectionRepository(final CommonSectionDao sectionDao, final CommonLineDao lineDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    public void save(final Long lineId, final SectionRequest request) {
        final Line line = lineDao.findById(lineId);
    }
}
