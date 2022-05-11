package wooteco.subway.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionLinks;
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
        SectionLinks sectionLinks = createStations(savedSections);
        Sections sections = new Sections(savedSections);
        sectionLinks.validateAddableSection(section);
        updateSection(section, sectionLinks, sections);
    }

    private SectionLinks createStations(List<Section> sections) {
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
        if (sectionLinks.isExistUpStation(section.getUpStationId())) {
            Section findSection = sections.getSameUpStationSection(section);
            validateDistance(section, findSection);
            sectionDao.updateByUpStationId(section);
            sectionDao.save(findSection.createExceptUpSection(section));
            return;
        }
        if (sectionLinks.isExistDownStation(section.getDownStationId())) {
            Section findSection = sections.getSameDownStationSection(section);
            validateDistance(section, findSection);
            sectionDao.updateByDownStationId(section);
            sectionDao.save(findSection.createExceptDownSection(section));
        }
    }

    private void validateDistance(Section section, Section existingSection) {
        if (!existingSection.isLong(section)) {
            throw new IllegalStateException("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
        }
    }

    public void delete(Long lineId, Long stationId) {
        List<Section> lineSections = sectionDao.findAllByLineId(lineId);
        SectionLinks sectionLinks = createStations(lineSections);
        Sections sections = new Sections(lineSections);
        sections.validateDeletableSize();
        if (sectionLinks.isExistUpStation(stationId) && sectionLinks.isExistDownStation(stationId)) {
            deleteAndSaveSections(lineId, stationId, sections);
            return;
        }
        if (sectionLinks.isEndStation(stationId)) {
            sectionDao.delete(lineId, stationId);
            return;
        }
        throw new NoSuchElementException("구간이 존재하지 않음");
    }

    private void deleteAndSaveSections(Long lineId, Long stationId, Sections sections) {
        Section upSection = sections.getSameUpStationSection(stationId);
        Section downSection = sections.getSameDownStationSection(stationId);
        sectionDao.delete(lineId, stationId);
        sectionDao.save(new Section(
            lineId,
            downSection.getUpStationId(),
            upSection.getDownStationId(),
            upSection.getDistance() + downSection.getDistance()
        ));
    }
}
