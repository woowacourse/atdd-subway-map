package wooteco.subway.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionLinks;
import wooteco.subway.domain.Sections;

@Service
public class SectionService {

    private static final String NOT_EXIST_STATION = "역이 존재하지 않습니다.";

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
        SectionLinks sectionLinks = createStationLinks(savedSections);
        Sections sections = new Sections(savedSections);
        sectionLinks.validateAddableSection(section);
        updateSection(section, sectionLinks, sections);
    }

    private SectionLinks createStationLinks(List<Section> sections) {
        Map<Long, Long> stationIds = new HashMap<>();
        for (Section existingSection : sections) {
            stationIds.put(existingSection.getUpStationId(), existingSection.getDownStationId());
        }
        return new SectionLinks(stationIds);
    }

    private void updateSection(Section section, SectionLinks sectionLinks, Sections sections) {
        if (sectionLinks.isEndSection(section)) {
            sectionDao.save(section);
            return;
        }
        updateMiddleSection(section, sectionLinks, sections);
    }

    private void updateMiddleSection(Section section, SectionLinks sectionLinks, Sections sections) {
        if (sectionLinks.isExistUpStation(section.getUpStationId())) {
            Section existing = sections.getSameUpStationSection(section);
            updateNewSections(existing, section, existing.createExceptUpSection(section));
            return;
        }
        if (sectionLinks.isExistDownStation(section.getDownStationId())) {
            Section existing = sections.getSameDownStationSection(section);
            updateNewSections(existing, section, existing.createExceptDownSection(section));
        }
    }

    private void updateNewSections(Section existingSection, Section additionalSection, Section newSection) {
        sectionDao.delete(existingSection.getId());
        sectionDao.save(additionalSection);
        sectionDao.save(newSection);
    }

    public void delete(Long lineId, Long stationId) {
        List<Section> lineSections = sectionDao.findAllByLineId(lineId);
        SectionLinks sectionLinks = createStationLinks(lineSections);
        Sections sections = new Sections(lineSections);
        if (sectionLinks.isNotExistStation(stationId)) {
            throw new IllegalArgumentException(NOT_EXIST_STATION);
        }
        deleteSection(lineId, stationId, sectionLinks, sections);
    }

    private void deleteSection(Long lineId, Long stationId, SectionLinks sectionLinks, Sections sections) {
        sections.validateDeletableSize();
        if (sectionLinks.isExistUpStation(stationId) && sectionLinks.isExistDownStation(stationId)) {
            deleteAndSaveSections(lineId, stationId, sections);
            return;
        }
        if (sectionLinks.isEndStation(stationId)) {
            sectionDao.delete(lineId, stationId);
        }
    }

    private void deleteAndSaveSections(Long lineId, Long stationId, Sections sections) {
        Section upSection = sections.getSameUpStationSection(stationId);
        Section downSection = sections.getSameDownStationSection(stationId);
        sectionDao.delete(lineId, stationId);
        sectionDao.save(upSection.createCombineSection(downSection));
    }
}
