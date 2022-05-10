package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Service
public class SectionService {
    //TODO Sections로 서비스 로직(Dao 로직도) 이동 및 테스트코드 이동
    private static final String INVALID_STATION_ID_ERROR_MESSAGE = "구간 안에 존재하지 않는 아이디의 역이 있습니다.";
    private static final String SECTION_LENGTH_ERROR_MESSAGE = "새 구간의 길이가 기존 역 사이 길이보다 작아야 합니다.";
    private static final String ONE_LESS_SECTION_ERROR_MESSAGE = "해당 지하철 노선은 1개 이하의 구간을 가지고 있어 역을 삭제할 수 없습니다.";

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Section save(Section section) {
        checkStationExist(section);
        Sections sections = new Sections(sectionDao.findAllSectionWithStationsByLineId(section.getLineId()));
        sections.validateSave(section);
        if (sections.isMiddleSection(section)) {
            boolean isUpAttach = sections.isMiddleUpAttachSection(section);
            Section baseSection = getBaseSection(section, isUpAttach);
            Long id = executeMiddleSection(section, isUpAttach, baseSection);
            return sectionDao.findById(id);
        }
        Long id = sectionDao.save(section);
        return sectionDao.findById(id);
    }

    private void checkStationExist(Section section) {
        if (!stationDao.hasStation(section.getUpStationId()) || !stationDao.hasStation(section.getDownStationId())) {
            throw new IllegalArgumentException(INVALID_STATION_ID_ERROR_MESSAGE);
        }
    }

    public Section findById(Long id) {
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

    private void validateDistance(int existingDistance, int sectionDistance) {
        if (sectionDistance >= existingDistance) {
            throw new IllegalArgumentException(SECTION_LENGTH_ERROR_MESSAGE);
        }
    }

    public List<Station> findStationsOfLine(Long lineId) {
        return new Sections(sectionDao.findAllSectionWithStationsByLineId(lineId)).calculateStations();
    }

    public void deleteSection(Long lineId, Long stationId) {
        validateTwoMoreSections(lineId);
        Sections sections = new Sections(sectionDao.findAllSectionWithStationsByLineId(lineId));
        if (sections.isFirstUpStation(stationDao.findById(stationId)) || sections.isLastDownStation(stationDao.findById(stationId))) {
            deleteSideStation(lineId, stationId, sections);
            return;
        }
        deleteMiddleSection(lineId, stationId);
    }

    private void deleteSideStation(Long lineId, Long stationId, Sections sections) {
        Station station = stationDao.findById(stationId);
        if (sections.isFirstUpStation(station)) {
            sectionDao.delete(sectionDao.findByUpStationId(lineId, stationId).getId());
        }
        if (sections.isLastDownStation(station)) {
            sectionDao.delete(sectionDao.findByDownStationId(lineId, stationId).getId());
        }
    }

    public void validateTwoMoreSections(Long lineId) {
        if (sectionDao.findAllByLineId(lineId).size() <= 1) {
            throw new IllegalArgumentException(ONE_LESS_SECTION_ERROR_MESSAGE);
        }
    }

    private void deleteMiddleSection(Long lineId, Long stationId) {
        Section upSection = sectionDao.findByDownStationId(lineId, stationId);
        Section downSection = sectionDao.findByUpStationId(lineId, stationId);
        sectionDao.delete(upSection.getId());
        sectionDao.delete(downSection.getId());
        int distance = upSection.getDistance() + downSection.getDistance();
        Section combinedSection = new Section(lineId, upSection.getUpStationId(), downSection.getDownStationId(),
                distance);
        sectionDao.save(combinedSection);
    }
}
