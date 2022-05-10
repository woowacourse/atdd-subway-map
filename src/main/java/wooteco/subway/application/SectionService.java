package wooteco.subway.application;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

@Service
public class SectionService {

    private final SectionDao<Section> sectionDao;

    public SectionService(SectionDao<Section> sectionDao) {
        this.sectionDao = sectionDao;
    }

    public int deleteSection(Long lineId, Long stationId) {
        return sectionDao.deleteSection(lineId, stationId);
    }
}
