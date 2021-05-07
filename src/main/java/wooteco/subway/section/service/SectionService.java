package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.LineRoute;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.dto.SectionRequest;

import java.util.List;
import java.util.Set;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void save(Long lineId, SectionRequest sectionRequest) {
        Section section = new Section(lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance()
        );
        List<Section> sectionsByLineId = sectionDao.findAllByLineId(section.getLineId());
        LineRoute lineRoute = new LineRoute(sectionsByLineId);
        Set<Long> sectionsIds = lineRoute.getStationIds();

        validateIfSectionContainsOnlyOneStationInLine(sectionsIds, section);

        if (lineRoute.isEndOfUpLine(section.getUpStationId()) || lineRoute.isEndOfDownLine(section.getDownStationId())) {
            sectionDao.save(section);
            return;
        }
        if (sectionsIds.contains(section.getDownStationId())) {
            updateSectionWhenIncludesDownStation(section, lineRoute);
            sectionDao.save(section);
        }
        if (sectionsIds.contains(section.getUpStationId())) {
            updateSectionWhenIncludesUpStation(section, lineRoute);
            sectionDao.save(section);
        }
    }

    private void updateSectionWhenIncludesUpStation(Section section, LineRoute lineRoute) {
        int prevDistance = lineRoute.getDistanceFromUpToDownStationMap(section.getUpStationId());
        int distanceGap = prevDistance - section.getDistance();
        validateDistantDifference(distanceGap);
        sectionDao.updateByUpStationId(section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                distanceGap);
    }

    private void updateSectionWhenIncludesDownStation(Section section, LineRoute lineRoute) {
        int prevDistance = lineRoute.getDistanceFromDownToUpStationMap(section.getDownStationId());
        int distanceGap = prevDistance - section.getDistance();
        validateDistantDifference(distanceGap);
        sectionDao.updateByDownStationId(section.getLineId(),
                section.getDownStationId(),
                section.getUpStationId(),
                distanceGap);
    }

    private void validateDistantDifference(int difference) {
        if (difference <= 0) {
            throw new IllegalArgumentException("입력하신 구간의 거리가 잘못되었습니다.");
        }
    }

    private void validateIfSectionContainsOnlyOneStationInLine(Set<Long> sectionsIds, Section section) {
        long count = sectionsIds.stream()
                .filter(sectionId -> sectionId.equals(section.getDownStationId()) || sectionId.equals(section.getUpStationId()))
                .count();

        if (count != 1) {
            throw new IllegalArgumentException("구간의 역 중에서 한개의 역만은 노선에 존재하여야 합니다.");
        }
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        List<Section> sectionsByLineId = sectionDao.findAllByLineId(lineId);
        LineRoute lineRoute = new LineRoute(sectionsByLineId);

        if (lineRoute.isEndOfLine(stationId)) {
            throw new IllegalArgumentException("종점은 삭제 할 수 없습니다.");
        }

        Section upSection = lineRoute.getSectionFromUpToDownStationMapByStationId(stationId);
        Section downSection = lineRoute.getSectionFromDownToUpStationMapByStationId(stationId);

        sectionDao.delete(upSection.getId());
        sectionDao.delete(downSection.getId());

        sectionDao.save(new Section(lineId,
                downSection.getUpStationId(),
                upSection.getDownStationId(),
                upSection.getDistance() + downSection.getDistance()));
    }
}
