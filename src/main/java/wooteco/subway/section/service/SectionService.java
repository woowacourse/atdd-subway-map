package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.InvalidInsertException;
import wooteco.subway.line.Line;
import wooteco.subway.line.repository.LineDao;
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
    private final LineDao lineDao;

    public SectionService(StationService stationService, SectionDao sectionDao, LineDao lineDao) {
        this.stationService = stationService;
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    @Transactional
    public void save(Line newLine, SectionRequest sectionReq) {
        stationService.validateExistStations(sectionReq.getUpStationId(), sectionReq.getDownStationId());
        sectionDao.save(sectionReq.toEntity(newLine));
    }

    @Transactional
    public SectionResponse appendSection(Long lineId, SectionRequest sectionReq) {
        stationService.validateExistStations(sectionReq.getUpStationId(), sectionReq.getDownStationId());
        Section newSection = sectionReq.toEntity(lineDao.findById(lineId));
        Sections sections =  new Sections(sectionDao.findAllByLineId(lineId), newSection);

        if (sections.isOnEdge(newSection)) {
            return saveAtEnd(newSection);
        }
        return saveAtMiddle(newSection, sections);
    }

    private SectionResponse saveAtEnd(Section newSection) {
        Section savedSection = sectionDao.save(newSection);
        return SectionResponse.from(savedSection);
    }

    private SectionResponse saveAtMiddle(Section newSection, Sections sections) {
        if (sections.appendToForward(newSection)) {
            Section changedSection = insertSectionToForward(newSection);
            return SectionResponse.from(changedSection);
        }
        if (sections.appendToBackward(newSection)) {
            Section changedSection = insertSectionToBackward(newSection);
            return SectionResponse.from(changedSection);
        }
        throw new InvalidInsertException("해당 구간에 추가할 수 없습니다.");
    }

    private Section insertSectionToBackward(Section newSection) {
        int changedDistance = compareDistanceWhenAppendToBottom(newSection);
        sectionDao.updateSectionToBackward(newSection, changedDistance);
        Section changedSection = sectionDao.save(newSection);
        return changedSection;
    }

    private Section insertSectionToForward(Section newSection) {
        int changedDistance = compareDistanceWhenAppendToUp(newSection);
        sectionDao.updateSectionToForward(newSection, changedDistance);
        Section changedSection = sectionDao.save(newSection);
        return changedSection;
    }

    private int compareDistanceWhenAppendToBottom(Section newSection) {
        Section currentSection = sectionDao.findByDownStationId(newSection.getLineId(), newSection.getDownStation());
        return currentSection.subtractDistance(newSection);
    }

    private int compareDistanceWhenAppendToUp(Section newSection) {
        Section currentSection = sectionDao.findByUpStationId(newSection.getLineId(), newSection.getUpStation());
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
        Station station = stationService.findBy(stationId);
        Line line = lineDao.findById(lineId);

        sections.removeSection(line, station);
        sectionDao.deleteByLineId(line);
        sections.toSortedSections().forEach(sectionDao::save);
    }
}
