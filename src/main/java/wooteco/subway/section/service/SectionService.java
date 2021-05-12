package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.SubWayException;
import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.dto.SectionResponse;
import wooteco.subway.section.repository.JdbcSectionDao;
import wooteco.subway.station.Station;

import java.util.List;

@Service
public class SectionService {
    private JdbcSectionDao jdbcSectionDao;

    public SectionService(JdbcSectionDao jdbcSectionDao) {
        this.jdbcSectionDao = jdbcSectionDao;
    }

    public SectionResponse save(Long lineId, SectionRequest sectionReq) {
        validateExistStation(sectionReq.toEntity());

        Section savedSection = jdbcSectionDao.save(lineId, sectionReq.toEntity());
        return new SectionResponse(savedSection);
    }

    public SectionResponse add(Long lineId, SectionRequest sectionReq) {
        Section newSection = sectionReq.toEntity();
        Sections sections =  new Sections(jdbcSectionDao.findAllByLineId(lineId));
        validateSavableSection(newSection, sections);

        if (sections.isOnEdge(newSection)) {
            return saveAtEnd(lineId, newSection);
        }
        return saveAtMiddle(lineId, newSection, sections);
    }

    private void validateSavableSection(Section newSection, Sections sections) {
        validateExistStation(newSection);
        sections.validateSavableSection(newSection);
    }

    private void validateExistStation(Section section) {
        List<Station> stations = jdbcSectionDao.findStationsBy(section.getUpStationId(), section.getDownStationId());
        if (stations.size() != 2) {
            throw new SubWayException("등록되지 않은 역은 상행 혹은 하행역으로 추가할 수 없습니다.");
        }
    }

    private SectionResponse saveAtEnd(Long lineId, Section newSection) {
        Section savedSection = jdbcSectionDao.save(lineId, newSection);
        return new SectionResponse(savedSection);
    }

    private SectionResponse saveAtMiddle(Long lineId, Section newSection, Sections sections) {
        if (sections.appendToUp(newSection)) {
            int changedDistance = compareDistanceWhenAppendToUp(lineId, newSection);
            return new SectionResponse(jdbcSectionDao.appendToUp(lineId, newSection, changedDistance));
        }
        if (sections.appendBeforeDown(newSection)) {
            int changedDistance = compareDistanceWhenAppendToBottom(lineId, newSection);
            return new SectionResponse(jdbcSectionDao.appendBeforeDown(lineId, newSection, changedDistance));
        }
        throw new SubWayException("구간 추가 불가능");
    }

    private int compareDistanceWhenAppendToBottom(Long lineId, Section newSection) {
        Section oldSection = jdbcSectionDao.findByDownStationId(lineId, newSection.getDownStationId());
        return differenceInDistance(newSection, oldSection);
    }

    private int compareDistanceWhenAppendToUp(Long lineId, Section newSection) {
        Section oldSection = jdbcSectionDao.findByUpStationId(lineId, newSection.getUpStationId());
        return differenceInDistance(newSection, oldSection);
    }

    private int differenceInDistance(Section newSection, Section oldSection) {
        validatesDistance(oldSection, newSection);
        return oldSection.getDistance() - newSection.getDistance();
    }

    private void validatesDistance(Section oldSection, Section newSection) {
        if (newSection.hasLongerDistanceThan(oldSection)) {
            throw new SubWayException("거리 때문에 등록할 수 없음");
        }
    }

    public List<Long> findAllSectionsId(Long lineId) {
        Sections sections = new Sections(jdbcSectionDao.findAllByLineId(lineId));
        return sections.toSortedStationIds();
    }

    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(jdbcSectionDao.findAllByLineId(lineId));
        sections.validateDeletable();
        if (sections.isOnUpEdge(stationId)) {
            jdbcSectionDao.deleteFirstSection(lineId, stationId);
            return;
        }

        if (sections.isOnDownEdge(stationId)) {
            jdbcSectionDao.deleteLastSection(lineId, stationId);
            return;
        }
        deleteSectionInMiddle(lineId, stationId, sections);
    }

    private void deleteSectionInMiddle(Long lineId, Long stationId, Sections sections) {
        Section before = sections.findSectionByDown(stationId);
        Section after = sections.findSectionByUp(stationId);

        Section newSection = makeNewSection(before, after);

        jdbcSectionDao.save(lineId, newSection);
        jdbcSectionDao.delete(before);
        jdbcSectionDao.delete(after);
    }

    private Section makeNewSection(Section before, Section after) {
        Long newUp = before.getUpStationId();
        Long newDown = after.getDownStationId();
        int totalDistance = before.plusDistance(after);

        Section newSection = new Section(newUp, newDown, totalDistance);
        return newSection;
    }
}
