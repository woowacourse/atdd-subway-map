package wooteco.subway.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.section.Section;

@Repository
public class SectionRepository {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionRepository(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public long save(Section section) {
        return sectionDao.save(section);
    }
}
