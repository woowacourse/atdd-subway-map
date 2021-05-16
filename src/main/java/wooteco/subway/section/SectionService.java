package wooteco.subway.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DeleteMinimumSizeException;
import wooteco.subway.exception.NoSuchDataException;
import wooteco.subway.exception.SectionInsertExistStationsException;
import wooteco.subway.exception.ShortDistanceException;
import wooteco.subway.line.LineEndPoint;
import wooteco.subway.line.SectionRequest;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

@Service
@Transactional
public class SectionService {

    private StationDao stationDao;
    private SectionDao sectionDao;

    @Autowired
    public SectionService(StationDao stationDao,
        SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineEndPoint findSectionEndPoint(long lineId) {

        List<Section> sections = sectionDao.findSectionsByLineId(lineId);
        return findEndPointInSections(sections);
    }

    private LineEndPoint findEndPointInSections(List<Section> sections) {
        Map<Long, Long> route = new HashMap<>();
        for (Section section : sections) {
            route.put(section.getUpStationId(),
                route.getOrDefault(section.getUpStationId(), 0L) + 1);

            route.put(section.getDownStationId(),
                route.getOrDefault(section.getDownStationId(), 0L) - 1);
        }

        SectionRoutes sectionRoutes = new SectionRoutes(route);
        return sectionRoutes.findEndPointInRoute(route);
    }

    public List<Station> findStationsByLineId(long lineId) {

        LineEndPoint sectionEndPoint = findSectionEndPoint(lineId);

        List<Station> stations = new ArrayList<>();

        Map<Long, Long> sectionEndToEndRoute = sectionDao.findSectionsByLineId(lineId)
            .stream().collect(Collectors.toMap(Section::getUpStationId,
                Section::getDownStationId));

        long targetStationId = sectionEndPoint.getUpStationId();
        while (sectionEndPoint.isNotDownStationId(targetStationId)) {
            stations.add(stationDao.findById(targetStationId));
            targetStationId = sectionEndToEndRoute.get(targetStationId);
        }
        stations.add(stationDao.findById(targetStationId));
        return stations;
    }

    public void insertSection(Long lineId, SectionRequest sectionRequest) {
        List<Station> stationsInLine = findStationsByLineId(lineId);
        LineEndPoint sectionEndPoint = findSectionEndPoint(lineId);

        if (sectionDao.hasStation(lineId, sectionRequest.getUpStationId()) &&
            sectionDao.hasStation(lineId, sectionRequest.getDownStationId())) {
            throw new SectionInsertExistStationsException("이미 존재하는 역입니다");
        }

        if (isInsertPointIsEnd(sectionRequest, sectionEndPoint)) {
            sectionDao.save(new Section(lineId, sectionRequest));
            return;
        }

        sectionIsNoEndPoint(stationsInLine, lineId, sectionRequest);
    }

    private void sectionIsNoEndPoint(List<Station> stationsInLine, Long lineId,
        SectionRequest sectionRequest) {
        Section upSection;

        for (Station station : stationsInLine) {
            if (station.isSameStationId(sectionRequest.getUpStationId())) {
                upStationBasedInsert(lineId, sectionRequest);
                return;
            }
            if (station.isSameStationId(sectionRequest.getDownStationId())) {
                downStationBasedInsert(lineId, sectionRequest);
                return;
            }
        }

        throw new NoSuchDataException("노선에 역이 존재하지 않습니다.");
    }

    private void downStationBasedInsert(Long lineId, SectionRequest sectionRequest) {
        Section upSection;
        upSection = sectionDao
            .findByDownStationId(lineId, sectionRequest.getDownStationId());
        if (upSection.getDistance() <= sectionRequest.getDistance()) {
            throw new ShortDistanceException("삽입하려는 구간의 거리가 너무 짧습니다.");
        }
        int newDistance = upSection.getDistance() - sectionRequest.getDistance();
        sectionDao.delete(lineId, upSection.getUpStationId());
        sectionDao.save(new Section(lineId,
            upSection.getUpStationId(),
            sectionRequest.getUpStationId(),
            newDistance));
        sectionDao.save(new Section(lineId, sectionRequest));
    }

    private void upStationBasedInsert(Long lineId, SectionRequest sectionRequest) {
        Section upSection;
        upSection = sectionDao.findByUpStationId(lineId, sectionRequest.getUpStationId());
        if (upSection.getDistance() <= sectionRequest.getDistance()) {
            throw new ShortDistanceException("삽입하려는 구간의 거리가 너무 짧습니다.");
        }
        int newDistance = upSection.getDistance() - sectionRequest.getDistance();
        sectionDao.delete(lineId, upSection.getUpStationId());
        sectionDao.save(new Section(lineId,
            sectionRequest.getDownStationId(),
            upSection.getDownStationId(),
            newDistance));
        sectionDao.save(new Section(lineId, sectionRequest));
    }

    private boolean isInsertPointIsEnd(SectionRequest sectionRequest,
        LineEndPoint sectionEndPoint) {
        return sectionEndPoint.getUpStationId() == sectionRequest.getDownStationId()
            || sectionEndPoint.getDownStationId() == sectionRequest.getUpStationId();
    }

    public List<Section> findSectionsInLineId(long lineId) {
        return sectionDao.findSectionsByLineId(lineId);
    }

    public Section findByUpStationId(long lineId, long upStationId) {
        return sectionDao.findByUpStationId(lineId, upStationId);
    }

    public Section findByDownStationId(long lineId, long upStationId) {
        return sectionDao.findByUpStationId(lineId, upStationId);
    }

    @Transactional
    public void deleteByUpStationId(long lineId, long upStationId) {
        LineEndPoint sectionEndPoint = findSectionEndPoint(lineId);

        if (sectionDao.findSectionsByLineId(lineId).size() < 2) {
            throw new DeleteMinimumSizeException("구간이 두 개 이상일 때에만 삭제할 수 있습니다.");
        }
        if (sectionEndPoint.isSameUpStationId(upStationId)) {
            sectionDao.delete(lineId, upStationId);
            return;
        }
        if (sectionEndPoint.isSameDownStationId(upStationId)) {
            sectionDao.delete(lineId,
                sectionDao.findByDownStationId(lineId, upStationId).getUpStationId());
            return;
        }
        if (sectionDao.hasStation(lineId, upStationId)) {
            deleteMiddleSection(lineId, upStationId);
            return;
        }
        throw new NoSuchDataException("존재하지 않는 역입니다");
    }

    private void deleteMiddleSection(long lineId, long upStationId) {
        Section expectedDeleteSection = sectionDao.findByUpStationId(lineId, upStationId);

        sectionDao.delete(lineId, upStationId);

        Section prevDeleteSection = sectionDao
            .findByDownStationId(lineId, expectedDeleteSection.getUpStationId());
        Long prevDeleteSectionId = sectionDao.findSectionId(lineId,
            prevDeleteSection.getUpStationId(), prevDeleteSection.getDownStationId());

        sectionDao.update(prevDeleteSectionId,
            expectedDeleteSection.getDownStationId(),
            expectedDeleteSection.getDistance() + prevDeleteSection
                .getDistance());
    }

    public void save(Section sectionAddDto) {
        sectionDao.save(sectionAddDto);
    }
}
