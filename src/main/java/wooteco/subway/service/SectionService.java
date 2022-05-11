package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section2;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.request.CreateSectionRequest;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.exception.NotFoundException;

@Service
public class SectionService {

    private static final String LINE_NOT_FOUND_EXCEPTION_MESSAGE = "해당되는 노선은 존재하지 않습니다.";
    private static final String LAST_SECTION_EXCEPTION = "노선의 마지막 구간은 제거할 수 없습니다.";
    private static final String STATION_NOT_FOUND_EXCEPTION_MESSAGE = "존재하지 않는 역을 입력하였습니다.";

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public void save(Long lineId, CreateSectionRequest sectionRequest) {
        Section2 newSection = sectionRequest.toSection();
        Sections sections = Sections.of(findValidSections(lineId));
        validateExistingStations(newSection);
        sections.validateSingleRegisteredStation(newSection);

        if (!sections.isEndSection(newSection)) {
            SectionEntity updatedPreviousSection = sections.toUpdatedSection(lineId, newSection);
            boolean containsRegisteredUpStation = sections.hasRegisteredUpStation(newSection);
            updatePreviousSection(updatedPreviousSection, containsRegisteredUpStation);
        }
        sectionDao.save(newSection.toEntityOf(lineId));
    }

    private void updatePreviousSection(SectionEntity updatedPreviousSection,
                                       boolean containsRegisteredUpStation) {
        if (containsRegisteredUpStation) {
            sectionDao.updateUpStationIdAndDistance(updatedPreviousSection);
            return;
        }
        sectionDao.updateDownStationIdAndDistance(updatedPreviousSection);
    }

    private void validateExistingStations(Section2 section) {
        List<Long> stationIds = section.getStationIds();
        List<StationEntity> stations = stationDao.findAllByIds(stationIds);
        if (stations.size() != stationIds.size()) {
            throw new NotFoundException(STATION_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        Sections sections = Sections.of(findValidSections(lineId));
        sections.validateRegisteredStation(stationId);
        validateNotLastSection(sections);

        sectionDao.deleteAllByLineIdAndStationId(lineId, stationId);
        if (sections.isMiddleStation(stationId)) {
            sectionDao.save(sections.toConnectedSectionBetween(lineId, stationId));
        }
    }

    private List<SectionEntity> findValidSections(Long lineId) {
        List<SectionEntity> sectionEntities = sectionDao.findAllByLineId(lineId);
        if (sectionEntities.isEmpty()) {
            throw new NotFoundException(LINE_NOT_FOUND_EXCEPTION_MESSAGE);
        }
        return sectionEntities;
    }

    private void validateNotLastSection(Sections sections) {
        if (sections.hasSingleSection()) {
            throw new IllegalArgumentException(LAST_SECTION_EXCEPTION);
        }
    }
}
