package wooteco.subway.service;

import java.util.List;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public List<Long> findAllStationIdByLineId(Long id) {
        Sections sections = new Sections(sectionDao.findAllByLineId(id));
        return sections.getAllStationId();
    }

    public void save(Section section) {
        Long lineId = section.getLineId();
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        if (sections.isEndSection(section)) {
            sectionDao.save(section);
            return;
        }
        Section editSection = sections.getSameStationSection(section);
        if (editSection.getUpStationId().equals(section.getUpStationId())) {
            sectionDao.updateByUpStationId(section);
            Section newSection = new Section(section.getLineId(), section.getDownStationId(),
                editSection.getDownStationId(), editSection.getDistance() - section.getDistance());
            sectionDao.save(newSection);
            return;
        }
        sectionDao.updateByDownStationId(section);
        Section newSection = new Section(section.getLineId(), section.getUpStationId(),
            editSection.getUpStationId(), editSection.getDistance() - section.getDistance());
        sectionDao.save(newSection);
    }
}
