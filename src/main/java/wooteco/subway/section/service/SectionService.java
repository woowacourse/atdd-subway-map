package wooteco.subway.section.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.LineRoute;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.exception.SectionIllegalArgumentException;

@Service
public class SectionService {

    public static final int DELETE_STATION_IN_LINE_LIMIT = 2;
    public static final int INSERT_SECTION_IN_LINE_LIMIT = 1;
    public static final int INSERT_SECTION_IN_LINE_DISTANCE_GAP_LIMIT = 0;
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void insertSectionInLine(Long lineId, SectionRequest sectionRequest) {
        Section section = Section.of(lineId, sectionRequest);

        List<Section> sectionsByLineId = sectionDao.findAllByLineId(section.getLineId());
        LineRoute lineRoute = new LineRoute(sectionsByLineId);
        Set<Long> sectionsIds = lineRoute.getStationIds();

        validateIfSectionContainsOnlyOneStationInLine(sectionsIds, section);

        if (lineRoute.isInsertSectionInEitherEndsOfLine(section)) {
            sectionDao.save(section);
            return;
        }
        insertSectionInMiddleOfLine(section, lineRoute);
        sectionDao.save(section);
    }

    private void validateIfSectionContainsOnlyOneStationInLine(Set<Long> sectionsIds,
        Section section) {
        long count = sectionsIds.stream()
            .filter(sectionId -> sectionId.equals(section.getDownStationId()) || sectionId
                .equals(section.getUpStationId()))
            .count();

        if (count != INSERT_SECTION_IN_LINE_LIMIT) {
            throw new SectionIllegalArgumentException("구간의 역 중에서 한개의 역만은 노선에 존재하여야 합니다.");
        }
    }

    private void insertSectionInMiddleOfLine(Section section, LineRoute lineRoute) {
        Section updateSection = lineRoute.getSectionNeedToBeUpdatedForInsert(section);
        validateSectionDistanceGap(updateSection);
        sectionDao.update(updateSection);
    }

    private void validateSectionDistanceGap(Section section) {
        if (section.getDistance() <= INSERT_SECTION_IN_LINE_DISTANCE_GAP_LIMIT) {
            throw new SectionIllegalArgumentException("입력하신 구간의 거리가 잘못되었습니다.");
        }
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        List<Section> sectionsByLineId = sectionDao.findAllByLineId(lineId);
        LineRoute lineRoute = new LineRoute(sectionsByLineId);

        if (lineRoute.getStationIds().size() == DELETE_STATION_IN_LINE_LIMIT) {
            throw new SectionIllegalArgumentException("종점은 삭제 할 수 없습니다.");
        }

        Optional<Section> upSection = lineRoute
            .getSectionFromUpToDownStationMapByStationId(stationId);
        Optional<Section> downSection = lineRoute
            .getSectionFromDownToUpStationMapByStationId(stationId);

        if (upSection.isPresent() && downSection.isPresent()) {
            sectionDao.save(Section.of(lineId,
                downSection.get().getUpStationId(),
                upSection.get().getDownStationId(),
                upSection.get().getDistance() + downSection.get().getDistance()));
            return;
        }

        upSection.ifPresent(section -> sectionDao.delete(section.getId()));
        downSection.ifPresent(section -> sectionDao.delete(section.getId()));
    }
}
