package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;

import java.util.Optional;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section save(Section section) {
        return sectionDao.save(section);
    }

    public Sections findByLineId(Long lineId) {
        return sectionDao.findByLineId(lineId);
    }

    public void addSection(Sections sections, Section section) {
        Section updateSection = sections.addSection(section);
        sectionDao.save(section);
        sectionDao.update(updateSection);
    }

    public void deleteSection(Sections sections, Long lineId, Long stationId) {
        Optional<Section> optSection = sections.deleteSection(lineId, stationId);
        sectionDao.delete(lineId, stationId);
        optSection.ifPresent(sectionDao::save);
    }
}
