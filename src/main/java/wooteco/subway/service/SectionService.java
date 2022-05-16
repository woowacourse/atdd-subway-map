package wooteco.subway.service;

import java.util.List;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(Section section) {
        Sections sections = new Sections(findAll(section.getLineId()));
        sections.validateAddable(section);
        executeSave(section, sections);
    }

    private List<Section> findAll(Long lineId) {
        return sectionDao.findAllByLineId(lineId);
    }

    private void executeSave(Section section, Sections sections) {
        if (sections.isEndSection(section)) {
            sectionDao.save(section);
            return;
        }
        Section searchedSection = sections.getMatchedSection(section);
        sectionDao.delete(searchedSection.getId());
        sectionDao.save(section);
        sectionDao.save(searchedSection.createExceptSection(section));
    }

    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(findAll(lineId));
        sections.validateDeletable(stationId);
        executeDelete(lineId, stationId, sections);
    }

    private void executeDelete(Long lineId, Long stationId, Sections sections) {
        sectionDao.delete(lineId, stationId);
        if (!sections.isEndStation(stationId)) {
            sectionDao.save(sections.createCombineSection(stationId));
        }
    }
}
