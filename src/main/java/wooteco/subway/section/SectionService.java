package wooteco.subway.section;

import org.springframework.stereotype.Service;
import wooteco.subway.section.dao.SectionDao;

@Service
public class SectionService {
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void deleteById(Long id, Long stationId) {
        Sections sections = new Sections(sectionDao.findById(id, stationId));
        sectionDao.deleteById(id, stationId);
        Long upStationId = sections.findUpStationId(stationId);
        Long downStationId = sections.findDownStationId(stationId);
        if(sections.isNotEndPoint()){
            sectionDao.save(new Section(id, upStationId, downStationId, sections.sumDistance()));
        }
    }
}
