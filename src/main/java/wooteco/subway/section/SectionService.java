package wooteco.subway.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.NoSuchDataException;
import wooteco.subway.exception.SectionInsertExistStationsException;
import wooteco.subway.exception.ShortDistanceException;
import wooteco.subway.line.LineDao;
import wooteco.subway.line.SectionRequest;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

@Service
public class SectionService {

    private StationDao stationDao;
    private LineDao lineDao;
    private SectionDao sectionDao;

    @Autowired
    public SectionService(StationDao stationDao, LineDao lineDao,
        SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public endPointInSection findSectionEndPoint(long lineId) {

        List<Section> sections = sectionDao.findSectionsByLineId(lineId);
        return findEndPointInSections(sections);
    }

    private endPointInSection findEndPointInSections(List<Section> sections) {
        Map<Long, Long> route = new HashMap<>();
        for (Section section : sections) {
            route.put(section.getUpStationId(),
                route.getOrDefault(section.getUpStationId(), 0L) + 1);

            route.put(section.getDownStationId(),
                route.getOrDefault(section.getDownStationId(), 0L) - 1);
        }
        return findEndPointInRoute(route);
    }

    private endPointInSection findEndPointInRoute(Map<Long, Long> route) {
        long upStationId = 0L;
        long downStationId = 0L;

        for (Entry<Long, Long> entry : route.entrySet()) {
            if (entry.getValue() == 1L) {
                upStationId = entry.getKey();
            }
            if (entry.getValue() == -1L) {
                downStationId = entry.getKey();
            }
        }
        return new endPointInSection(upStationId, downStationId);
    }


    public List<Station> findStationsInLine(long lineId) {
        endPointInSection sectionEndPoint = findSectionEndPoint(lineId);

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

    @Transactional
    public void insertSection(Long lineId, SectionRequest sectionRequest) {
        List<Station> stationsInLine = findStationsInLine(lineId);
        endPointInSection sectionEndPoint = findSectionEndPoint(lineId);

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
                return;
            }
            if (station.isSameStationId(sectionRequest.getDownStationId())) {
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
                return;
            }
        }

        throw new NoSuchDataException("노선에 역이 존재하지 않습니다.");
    }

    private boolean isInsertPointIsEnd(SectionRequest sectionRequest,
        endPointInSection sectionEndPoint) {
        return sectionEndPoint.getUpStationId() == sectionRequest.getDownStationId()
            || sectionEndPoint.getDownStationId() == sectionRequest.getUpStationId();
    }

    public List<Section> findSectionsInLine(long lineId) {
        return sectionDao.findSectionsByLineId(lineId);
    }

    public Section findByUpStationId(long lineId, long upStationId) {
        return sectionDao.findByUpStationId(lineId, upStationId);
    }

    public Section findByDownStationId(long lineId, long upStationId) {
        return sectionDao.findByUpStationId(lineId, upStationId);
    }
}
