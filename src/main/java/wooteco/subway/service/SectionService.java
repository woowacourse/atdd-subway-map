package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public List<Station> findUniqueStationsByLineId(final Long lineId) {
        return sectionDao.findAllByLineId(lineId)
            .stream()
            .flatMap(section -> Stream.of(section.getUpStationId(), section.getDownStationId()))
            .distinct()
            .map(id -> stationDao.findById(id).get())
            .sorted()
            .collect(Collectors.toList());
    }
}
