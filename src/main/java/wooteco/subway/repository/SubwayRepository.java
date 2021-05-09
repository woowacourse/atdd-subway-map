package wooteco.subway.repository;

import org.springframework.stereotype.Component;
import wooteco.subway.domain.Section;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.dao.StationDao;

@Component
public class SubwayRepository {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public SubwayRepository(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public void insertSectionWithLineId(Section section) {
        sectionDao.insert(section);
    }
}
