package wooteco.subway.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.constant.TerminalStation;
import wooteco.subway.exception.constant.SectionNotRegisterException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Service
@AllArgsConstructor
public class SectionService {

    public static final int ONLY_ONE_SECTION = 1;
    private final SectionDao sectionDao;

    public long createSection(Section section) {
        List<Section> sections = sectionDao.findByLineId(section.getLineId());
        Section sectionWithSameUpStation = getSectionWithSameUpStation(sections, section.getUpStationId());
        Section sectionWithSameDownStation = getSectionWithSameDownStation(sections, section.getDownStationId());
        Map<TerminalStation, Long> terminalStations = findTerminalStations(section.getLineId());

        if (conditionForTerminalRegistration(section, terminalStations)) {
            return sectionDao.save(section);
        }

        if (conditionForInsertStationInMiddle(section.getDistance(), sectionWithSameUpStation)) {
            return addDownStationInMiddle(section, sectionWithSameUpStation);
        }

        if (conditionForInsertStationInMiddle(section.getDistance(), sectionWithSameDownStation)) {
            return addUpStationInMiddle(section, sectionWithSameDownStation);
        }

        validateUpAndDownStationAllExist(sectionWithSameUpStation, sectionWithSameDownStation);
        validateUpAndDownStationNotAllExist(sections, sectionWithSameUpStation, sectionWithSameDownStation);

        return sectionDao.save(section);
    }

    public LinkedList<Long> findSortedStationIds(long lineId) {
        List<Section> foundSections = sectionDao.findByLineId(lineId);
        Sections sections = new Sections(foundSections);
        return sections.getSorted();
    }


    public Map<TerminalStation, Long> findTerminalStations(long lineId) {
        LinkedList<Long> sortedStations = findSortedStationIds(lineId);
        return Map.of(TerminalStation.UP, sortedStations.getFirst(), TerminalStation.DOWN, sortedStations.getLast());
    }

    public void deleteSection(long lineId, long stationId) {
        List<Section> found = sectionDao.findByLineId(lineId);
        Sections sections = new Sections(found);
        sections.validateCanDeleteSection();

        Section upperSection = sections.getUpperSection(stationId);
        Section lowerSection = sections.getLowerSection(stationId);

        sections.joinSection(lowerSection, upperSection);

        sectionDao.update(lowerSection);
        sectionDao.deleteSection(upperSection);
    }

    private boolean conditionForTerminalRegistration(Section section, Map<TerminalStation, Long> terminalStations) {
        return terminalStations.get(TerminalStation.UP).equals(section.getDownStationId())
                || terminalStations.get(TerminalStation.DOWN).equals(section.getUpStationId());
    }

    private void validateUpAndDownStationAllExist(Section sectionWithSameUpStation, Section sectionWithSameDownStation) {
        if (sectionWithSameUpStation != null && sectionWithSameDownStation != null) {
            throw new SectionNotRegisterException();
        }
    }

    private void validateUpAndDownStationNotAllExist(List<Section> sections, Section sectionWithSameUpStation, Section sectionWithSameDownStation) {
        if (sectionWithSameUpStation == null && sectionWithSameDownStation == null) {
            throw new SectionNotRegisterException();
        }
    }

    private boolean conditionForInsertStationInMiddle(Integer distance, Section section) {
        if (section == null) {
            return false;
        }
        if (distance >= section.getDistance()) {
            throw new SectionNotRegisterException();
        }
        return true;
    }

    private long addDownStationInMiddle(Section section, Section existingSection) {
        long createdSectionId = sectionDao.save(section);

        existingSection.setUpStationId(section.getDownStationId());
        existingSection.setDistance(existingSection.getDistance() - section.getDistance());
        sectionDao.update(existingSection);

        return createdSectionId;
    }

    private Long addUpStationInMiddle(Section section, Section existingSection) {
        long createdSectionId = sectionDao.save(section);

        existingSection.setDownStationId(section.getUpStationId());
        existingSection.setDistance(existingSection.getDistance() - section.getDistance());
        sectionDao.update(existingSection);

        return createdSectionId;
    }


    private Section getSectionWithCondition(List<Section> sections, Predicate<Section> condition) {
        return sections.stream()
                .filter(condition)
                .findAny()
                .orElse(null);
    }

    private Section getSectionWithSameUpStation(List<Section> sections, Long upStationId) {
        return getSectionWithCondition(sections, section -> section.getUpStationId().equals(upStationId));
    }

    private Section getSectionWithSameDownStation(List<Section> sections, Long downStationId) {
        return getSectionWithCondition(sections, section -> section.getDownStationId().equals(downStationId));
    }
}
