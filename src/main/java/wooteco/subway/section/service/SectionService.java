package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.InvalidInsertException;
import wooteco.subway.line.Line;
import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.dto.SectionResponse;
import wooteco.subway.section.repository.SectionDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.service.StationService;

import java.util.List;

@Service
public class SectionService {
    private final StationService stationService;
    private final SectionDao sectionDao;

    public SectionService(StationService stationService, SectionDao sectionDao) {
        this.stationService = stationService;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void save(Line newLine, SectionRequest sectionReq) {
        stationService.validateExistStations(sectionReq.getUpStationId(), sectionReq.getDownStationId());
        sectionDao.save(newLine.getId(), sectionReq.toEntity());
    }

    @Transactional
    public SectionResponse appendSection(Long lineId, SectionRequest sectionReq) {
        stationService.validateExistStations(sectionReq.getUpStationId(), sectionReq.getDownStationId());
        Section newSection = sectionReq.toEntity();
        Sections sections =  new Sections(sectionDao.findAllByLineId(lineId), newSection);

        if (sections.isOnEdge(newSection)) {
            return saveAtEnd(lineId, newSection);
        }
        return saveAtMiddle(lineId, newSection, sections);
    }

    private SectionResponse saveAtEnd(Long lineId, Section newSection) {
        Section savedSection = sectionDao.save(lineId, newSection);
        return SectionResponse.from(savedSection);
    }

    private SectionResponse saveAtMiddle(Long lineId, Section newSection, Sections sections) {
        if (sections.appendToForward(newSection)) {
            Section changedSection = insertSectionToForward(lineId, newSection);
            return SectionResponse.from(changedSection);
        }
        if (sections.appendToBackward(newSection)) {
            Section changedSection = insertSectionToBackward(lineId, newSection);
            return SectionResponse.from(changedSection);
        }
        throw new InvalidInsertException("해당 구간에 추가할 수 없습니다.");
    }

    private Section insertSectionToBackward(Long lineId, Section newSection) {
        int changedDistance = compareDistanceWhenAppendToBottom(lineId, newSection);
        sectionDao.updateSectionToBackward(lineId, newSection, changedDistance);
        Section changedSection = sectionDao.save(lineId, newSection);
        return changedSection;
    }

    private Section insertSectionToForward(Long lineId, Section newSection) {
        int changedDistance = compareDistanceWhenAppendToUp(lineId, newSection);
        sectionDao.updateSectionToForward(lineId, newSection, changedDistance);
        Section changedSection = sectionDao.save(lineId, newSection);
        return changedSection;
    }

    private int compareDistanceWhenAppendToBottom(Long lineId, Section newSection) {
        Section currentSection = sectionDao.findByDownStationId(lineId, newSection.getDownStation());
        return currentSection.subtractDistance(newSection);
    }

    private int compareDistanceWhenAppendToUp(Long lineId, Section newSection) {
        Section currentSection = sectionDao.findByUpStationId(lineId, newSection.getUpStation());
        return currentSection.subtractDistance(newSection);
    }

    @Transactional(readOnly = true)
    public List<Long> findAllSectionsId(Long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        return sections.toSortedStationIds();
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.validateDeletable();
        if (sections.isOnUpEdge(stationId)) {
            sectionDao.deleteFirstSection(lineId, stationId);
            return;
        }

        if (sections.isOnDownEdge(stationId)) {
            sectionDao.deleteLastSection(lineId, stationId);
            return;
        }
        deleteSectionInMiddle(lineId, stationId, sections);
    }

    private void deleteSectionInMiddle(Long lineId, Long stationId, Sections sections) {
        Section before = sections.findSectionByDown(stationId);
        Section after = sections.findSectionByUp(stationId);

        Section newSection = makeNewSection(before, after);

        sectionDao.save(lineId, newSection);
        sectionDao.delete(before);
        sectionDao.delete(after);
    }

    private Section makeNewSection(Section before, Section after) {
        Station newUp = before.getUpStation();
        Station newDown = after.getDownStation();
        int totalDistance = before.plusDistance(after);

        Section newSection = new Section(newUp, newDown, totalDistance);
        return newSection;
    }
}
