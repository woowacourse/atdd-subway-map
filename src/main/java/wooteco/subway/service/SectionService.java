package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionWithStation;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Service
public class SectionService {
    private static final String SECTION_LENGTH_ERROR_MESSAGE = "새 구간의 길이가 기존 역 사이 길이보다 작아야 합니다.";
    private static final String DUPLICATED_SECTION_ERROR_MESSAGE = "중복된 구간입니다.";
    private static final String INVALID_STATION_ID_ERROR_MESSAGE = "구간 안에 존재하지 않는 아이디의 역이 있습니다.";
    private static final String LINK_FAILURE_ERROR_MESSAGE = "해당 구간은 역과 연결될 수 없습니다.";
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Section save(Section section) {
        Sections sections = getSections(section.getLineId());
        validateSection(section);
        if (sections.isMiddleSection(section)) {
            boolean isUpAttach = sections.isMiddleUpAttachSection(section);
            Section baseSection = getBaseSection(section, isUpAttach);
            Long id = executeMiddleSection(section, isUpAttach, baseSection);
            return sectionDao.findById(id);
        }
        Long id = sectionDao.save(section);
        return sectionDao.findById(id);
    }

    private Long executeMiddleSection(Section inSection, boolean isUpAttach, Section baseSection) {
        validateDistance(baseSection.getDistance(), inSection.getDistance());
        sectionDao.save(baseSection.calculateRemainSection(inSection, isUpAttach));
        sectionDao.delete(baseSection.getId());
        return sectionDao.save(inSection);
    }

    private Section getBaseSection(Section section, boolean isUpAttach) {
        if (isUpAttach) {
            return sectionDao.findByUpStationId(section.getLineId(), section.getUpStationId());
        }
        return sectionDao.findByDownStationId(section.getLineId(), section.getDownStationId());
    }

    private void validateSection(Section section) {
        checkUniqueSection(section);
        checkStationExist(section);
        checkIsLinked(section);
    }

    private void checkUniqueSection(Section section) {
        if (sectionDao.hasUpStationId(section) && sectionDao.hasDownStationId(section)) {
            throw new IllegalArgumentException(DUPLICATED_SECTION_ERROR_MESSAGE);
        }
    }

    private void checkStationExist(Section section) {
        if (!stationDao.hasStation(section.getUpStationId()) || !stationDao.hasStation(section.getDownStationId())) {
            throw new IllegalArgumentException(INVALID_STATION_ID_ERROR_MESSAGE);
        }
    }

    private void checkIsLinked(Section section) {
        if (sectionDao.findAllByLineId(section.getLineId()).size() != 0
                && hasNoStationId(section) && hasNoStationId(section.getReverseSection())) {
            throw new IllegalArgumentException(LINK_FAILURE_ERROR_MESSAGE);
        }
    }

    private boolean hasNoStationId(Section section) {
        return !sectionDao.hasUpStationId(section) && !sectionDao.hasDownStationId(section);
    }

    private void validateDistance(int existingDistance, int sectionDistance) {
        if (sectionDistance >= existingDistance) {
            throw new IllegalArgumentException(SECTION_LENGTH_ERROR_MESSAGE);
        }
    }

    public List<Station> findStationsOfLine(Long lineId) {
        return getSections(lineId).calculateStations();
    }

    private Sections getSections(Long lineId) {
        return new Sections(sectionDao.findAllByLineId(lineId).stream()
                .map(this::getSectionWithStation)
                .collect(Collectors.toList())
        );
    }

    private SectionWithStation getSectionWithStation(Section section) {
        return SectionWithStation.of(section, stationDao.findById(section.getUpStationId()),
                stationDao.findById(section.getDownStationId()));
    }

    public void deleteSection(Long lineId, Long stationId) {
        //TODO 구간제거 기능 추가
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        boolean isUpStation = sections.stream().anyMatch(section -> section.getUpStationId().equals(stationId));
        boolean isDownStation = sections.stream().anyMatch(section -> section.getDownStationId().equals(stationId));
        //

    }
}
