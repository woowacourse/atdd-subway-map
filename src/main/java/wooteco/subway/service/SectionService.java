package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void insertSection(Long lineId, SectionRequest sectionRequest) {
        Section section = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Sections newSections = sections.add(section);
        sectionDao.deleteByLineId(lineId);
        sectionDao.save(lineId, newSections.getValue());
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Sections newSections = sections.delete(lineId, stationId);
        sectionDao.deleteByLineId(lineId);
        sectionDao.save(lineId, newSections.getValue());
    }
}
