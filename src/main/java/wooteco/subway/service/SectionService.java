package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.LineRoute;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.SubwayIllegalArgumentException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SectionService {

    public static final int DELETE_STATION_IN_LINE_LIMIT = 2;

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional(readOnly = false)
    public void insertSectionInLine(Long lineId, SectionRequest sectionRequest) {
        Section section = Section.of(lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());

        List<Section> sectionsByLineId = sectionDao.findAllByLineId(section.getLineId());
        LineRoute lineRoute = new LineRoute(sectionsByLineId);

        lineRoute.validateIfSectionContainsOnlyOneStationInLine(section);

        if (lineRoute.isInsertSectionInEitherEndsOfLine(section)) {
            sectionDao.save(section);
            return;
        }
        insertSectionInMiddleOfLine(section, lineRoute);
        sectionDao.save(section);
    }

    private void insertSectionInMiddleOfLine(Section section, LineRoute lineRoute) {
        Section updateSection = lineRoute.getSectionNeedToBeUpdatedForInsert(section);
        sectionDao.update(updateSection);
    }

    @Transactional(readOnly = false)
    public void delete(Long lineId, Long stationId) {
        List<Section> sectionsByLineId = sectionDao.findAllByLineId(lineId);
        LineRoute lineRoute = new LineRoute(sectionsByLineId);

        if (lineRoute.getStationIds().size() == DELETE_STATION_IN_LINE_LIMIT) {
            throw new SubwayIllegalArgumentException("구간이 하나인 노선에서 역은 더이상 삭제 할 수 없습니다.");
        }

        Optional<Section> upSection = lineRoute.getSectionFromUpToDownStationMapByStationId(stationId);
        Optional<Section> downSection = lineRoute.getSectionFromDownToUpStationMapByStationId(stationId);

        if (upSection.isPresent() && downSection.isPresent()) {
            sectionDao.save(Section.of(lineId,
                    downSection.get(),
                    upSection.get()));
        }

        upSection.ifPresent(section -> sectionDao.delete(section.getId()));
        downSection.ifPresent(section -> sectionDao.delete(section.getId()));
    }
}
