package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.common.exception.bad_request.WrongStationInfoException;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.List;

@Service
public class StationService {
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public Station save(Station station) {
        if (isDuplicatedName(station)) {
            throw new WrongStationInfoException(String.format("역 이름이 중복되었습니다. 중복된 역 이름 : %s", station.getName()));
        }
        return stationDao.save(station);
    }

    private boolean isDuplicatedName(Station station) {
        return stationDao.checkExistName(station.getName());
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Station findById(Long id) {
        return stationDao.findById(id);
    }

    public void delete(Station station) {
        if (sectionDao.existsStationInSection(station.getId())) {
            throw new WrongStationInfoException(String.format("역이 구간에 등록되어 있습니다. 역 ID: %d", station.getId()));
        }
        stationDao.delete(station);
    }

    public void deleteAll() {
        stationDao.deleteAll();
    }
}
