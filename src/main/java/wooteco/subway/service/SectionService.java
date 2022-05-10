package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public void create(Section section) {
        Sections sections = sectionDao.findByLineId(section.getLineId());
        Section savedSection = sectionDao.save(
                new Section(section.getLineId(), section.getUpStationId(), section.getDownStationId(),
                        section.getDistance()));

        sections.validateAddable(section);
        Section updateSection = sections.add(savedSection);
        sectionDao.updateSection(updateSection);
    }
}
