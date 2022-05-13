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
        List<Section> savedSections = sectionDao.findAllByLineId(section.getLineId());
        Sections sections = new Sections(savedSections);
        sections.validateAddable(section);
        executeSave(section, sections);
    }

    private void executeSave(Section section, Sections sections) {
        if (sections.isEndSection(section)) {
            sectionDao.save(section);
            return;
        }
        Section searchedSection = sections.searchMatchedSection(section);
        sectionDao.delete(searchedSection.getId());
        sectionDao.save(section);
        sectionDao.save(searchedSection.createExceptSection(section));
    }

    public void delete(Long lineId, Long stationId) {
        List<Section> lineSections = sectionDao.findAllByLineId(lineId);
        Sections sections = new Sections(lineSections);
        sections.validateDeletable(stationId);
        executeDelete(lineId, stationId, sections);
    }

    private void executeDelete(Long lineId, Long stationId, Sections sections) {
        if (sections.isEndStation(stationId)) {
            sectionDao.delete(lineId, stationId);
            return;
        }
        sectionDao.delete(lineId, stationId);
        sectionDao.save(sections.createCombineSection(stationId));
    }
}
