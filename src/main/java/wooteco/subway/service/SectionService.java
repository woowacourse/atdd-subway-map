package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Sections;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.exception.NotFoundException;

@Service
public class SectionService {

    // TODO: 중복제거
    private static final String LINE_NOT_FOUND_EXCEPTION_MESSAGE = "해당되는 노선은 존재하지 않습니다.";
    private static final String INVALID_REGISTERED_STATION_EXCEPTION = "구간에 등록되지 않은 지하철역입니다.";
    private static final String LAST_SECTION_EXCEPTION = "노선의 마지막 구간은 제거할 수 없습니다.";

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        Sections sections = Sections.of(findValidSections(lineId));
        validateExistingStation(stationId, sections);
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

    private void validateExistingStation(Long stationId, Sections sections) {
        if (sections.isExistingStation(stationId)) {
            throw new IllegalArgumentException(INVALID_REGISTERED_STATION_EXCEPTION);
        }
    }

    private void validateNotLastSection(Sections sections) {
        if (sections.hasSingleSection()) {
            throw new IllegalArgumentException(LAST_SECTION_EXCEPTION);
        }
    }
}
