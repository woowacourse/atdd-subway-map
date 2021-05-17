package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.domain.Station;

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

    public void addSection(Section section, Long id) {
        Sections sections = findByLineId(id);
        Section updateSection = sections.addSection(section);
        sectionDao.save(section);
        sectionDao.update(updateSection);
    }

    public void deleteSection(Sections sections, Long lineId, Station station) {
        Optional<Section> optSection = sections.findUpdateSectionAfterDelete(lineId, station);
        sectionDao.delete(lineId, station);
        optSection.ifPresent(sectionDao::save);
    }
}
