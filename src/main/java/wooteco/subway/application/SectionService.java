package wooteco.subway.application;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.constant.TerminalStation;
import wooteco.subway.exception.constant.SectionNotRegisterException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public long createSection(Section section) {
        List<Section> sections = sectionDao.findByLineId(section.getLineId());
        Section sectionWithSameUpStation = getSectionWithSameUpStation(sections, section.getUpStationId());
        Section sectionWithSameDownStation = getSectionWithSameDownStation(sections, section.getDownStationId());
        Map<TerminalStation, Long> terminalStations = findTerminalStations(section.getLineId());

        if (conditionForTerminalRegistration2(section, terminalStations)) {
            return sectionDao.save(section);
        }

        if (conditionForInsertStationInMiddle(section.getDistance(), sectionWithSameUpStation)) {
            return insertDownStationInMiddle(section, sectionWithSameUpStation);
        }

        if (conditionForInsertStationInMiddle(section.getDistance(), sectionWithSameDownStation)) {
            return insertUpStationInMiddle(section, sectionWithSameDownStation);
        }

        validateUpAndDownStationAllExist(sectionWithSameUpStation, sectionWithSameDownStation);
        validateUpAndDownStationNotAllExist(sections, sectionWithSameUpStation, sectionWithSameDownStation);

        return sectionDao.save(section);
    }

    public LinkedList<Long> findSortedStationIds(long lineId) {
        List<Section> foundSections = sectionDao.findByLineId(lineId);
        Map<Long, Long> toDownSectionMap = convertListToDownSectionMap(foundSections);
        Map<Long, Long> toUpSectionMap = convertListToUpSectionMap(foundSections);

        LinkedList<Long> result = new LinkedList<>();
        Long pivot = getAnyPivot(foundSections);
        Long iterator = pivot;
        result.add(pivot);

        insertDownSections(toDownSectionMap, result, iterator);

        iterator = pivot;
        insertUpSections(toUpSectionMap, result, iterator);
        return result;
    }

    public Map<TerminalStation, Long> findTerminalStations(long lineId) {
        LinkedList<Long> sortedStations = findSortedStationIds(lineId);
        return Map.of(TerminalStation.UP, sortedStations.getFirst(), TerminalStation.DOWN, sortedStations.getLast());
    }

    public void deleteSection(long lineId, long stationId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        Section upperSection = sections.stream()
                .filter(it -> it.getDownStationId().equals(stationId))
                .findAny()
                .get();

        Section lowerSection = sections.stream()
                .filter(it -> it.getUpStationId().equals(stationId))
                .findAny()
                .get();

        Long upStationId = upperSection.getUpStationId();
        lowerSection.setUpStationId(upStationId);
        lowerSection.setDistance(upperSection.getDistance() + lowerSection.getDistance());

        sectionDao.update(lowerSection);
        sectionDao.deleteSection(upperSection);
    }

    private boolean conditionForTerminalRegistration(Long upStationId, Long downStationId, Map<TerminalStation, Long> terminalStations) {
        return terminalStations.get(TerminalStation.UP).equals(downStationId)
                || terminalStations.get(TerminalStation.DOWN).equals(upStationId);
    }

    private boolean conditionForTerminalRegistration2(Section section, Map<TerminalStation, Long> terminalStations) {
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

    private long insertDownStationInMiddle(Section section, Section existingSection) {
        long createdSectionId = sectionDao.save(section);

        existingSection.setUpStationId(section.getDownStationId());
        existingSection.setDistance(existingSection.getDistance() - section.getDistance());
        sectionDao.update(existingSection);

        return createdSectionId;
    }

    private Long insertUpStationInMiddle(Section section, Section existingSection) {
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

    private long saveSection(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        return sectionDao.save(new Section(upStationId, downStationId, distance, lineId));
    }

    private long saveSection2(Section section) {
        return sectionDao.save(section);
    }

    private Long getAnyPivot(List<Section> foundSections) {
        return foundSections.get(0).getUpStationId();
    }

    private Map<Long, Long> convertListToUpSectionMap(List<Section> foundSections) {
        return foundSections.stream()
                .collect(Collectors.toMap(section -> section.getDownStationId(), section -> section.getUpStationId()));
    }

    private Map<Long, Long> convertListToDownSectionMap(List<Section> foundSections) {
        return foundSections.stream()
                .collect(Collectors.toMap(section -> section.getUpStationId(), section -> section.getDownStationId()));
    }

    private void insertUpSections(Map<Long, Long> toUpSection, LinkedList<Long> result, Long iterator) {
        while (true) {
            Long upSectionId = toUpSection.get(iterator);
            if (upSectionId == null) {
                break;
            }
            result.addFirst(upSectionId);
            iterator = upSectionId;
        }
    }

    private void insertDownSections(Map<Long, Long> toDownSection, LinkedList<Long> result, Long iterator) {
        while (true) {
            Long downSectionId = toDownSection.get(iterator);
            if (downSectionId == null) {
                break;
            }
            result.addLast(downSectionId);
            iterator = downSectionId;
        }
    }
}
