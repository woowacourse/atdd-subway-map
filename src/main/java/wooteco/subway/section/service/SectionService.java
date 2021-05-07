package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.domain.LineRoute;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.dto.SectionRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(Long lineId, SectionRequest sectionRequest) {
        Section section = new Section(lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance()
        );
        List<Section> sectionsByLineId = sectionDao.findAllByLineId(section.getLineId());

        Set<Long> sectionsIds = new HashSet<>();
        for (Section sec : sectionsByLineId) {
            sectionsIds.add(sec.getDownStationId());
            sectionsIds.add(sec.getUpStationId());
        }
        validateIfSectionContainsOnlyOneStationInLine(sectionsIds, section);
        LineRoute lineStations = LineRoute.from(sectionsByLineId);

        if (lineStations.isEndOfDownStation(section.getUpStationId()) || lineStations.isEndOfUpStation(section.getDownStationId())) {
            sectionDao.save(section);
            return;
        }
        if (sectionsIds.contains(section.getDownStationId())) {
            int prevDistance = lineStations.getDistanceFromDownToUpStationMap(section.getDownStationId());
            int difference = prevDistance - section.getDistance();
            if (difference <= 0) {
                throw new IllegalArgumentException("입력하신 구간의 거리가 잘못되었습니다.");
            }
            sectionDao.updateByDownStationId(section.getLineId(), section.getDownStationId(),
                    section.getUpStationId(), difference);
            sectionDao.save(section);
            return;
        }
        if (sectionsIds.contains(section.getUpStationId())) {
            int prevDistance = lineStations.getDistanceFromUpToDownStationMap(section.getUpStationId());
            int difference = prevDistance - section.getDistance();
            if (difference <= 0) {
                throw new IllegalArgumentException("입력하신 구간의 거리가 잘못되었습니다.");
            }
            sectionDao.updateByUpStationId(section.getLineId(), section.getUpStationId(), section.getDownStationId(),
                    difference);
            sectionDao.save(section);
            return;
        }
    }

    private void validateIfSectionContainsOnlyOneStationInLine(Set<Long> sectionsIds, Section section) {
        int count = 0;
        if (sectionsIds.contains(section.getDownStationId())) {
            ++count;
        }
        if (sectionsIds.contains(section.getUpStationId())) {
            ++count;
        }
        if (count != 1) {
            throw new IllegalArgumentException("구간의 역 중에서 한개의 역만은 노선에 존재하여야 합니다.");
        }
    }

    public void delete(Long lineId, Long stationId) {
        List<Section> sectionsByLineId = sectionDao.findAllByLineId(lineId);
        LineRoute lineRoute = LineRoute.from(sectionsByLineId);

        if (lineRoute.isEndOfDownStation(stationId) || lineRoute.isEndOfUpStation(stationId)) {
            throw new IllegalArgumentException("종점은 삭제 할 수 없습니다.");
        }

        Section upSection = lineRoute.getSectionFromUpToDownStationMapByStationId(stationId);
        Section downSection = lineRoute.getSectionFromDownToUpStationMapByStationId(stationId);

        sectionDao.delete(upSection.getId());
        sectionDao.delete(downSection.getId());

        Section newSection = new Section(lineId, downSection.getUpStationId(), upSection.getDownStationId(),
                upSection.getDistance() + downSection.getDistance());

        sectionDao.save(newSection);
    }
}
