package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.section.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public void save(Long lineId, SectionRequest sectionRequest) {
        stationDao.findById(sectionRequest.getUpStationId());
        stationDao.findById(sectionRequest.getDownStationId());

        Section section = Section.of(lineId, sectionRequest);
        sectionDao.save(section);
    }

}
