package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.exception.StationException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StationService {
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationService(final StationDao stationDao, final SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public Station save(final Station station) {
        validateName(station.getName());

        final Long id = stationDao.save(station.getName());
        return findById(id);
    }

    public void delete(final Long id) {
        final Optional optionalStation = stationDao.findById(id);

        if (optionalStation.isPresent()) {
            checkIsNotInLine(id);
            stationDao.delete(id);
            return;
        }

        throw new StationException("존재하지 않는 역입니다.");
    }

    public Station findById(final Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new StationException("존재하지 않는 역입니다."));
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    private void validateName(final String name) {
        if (stationDao.isExistingName(name)) {
            throw new StationException("이미 존재하는 역 이름입니다.");
        }
    }

    private void checkIsNotInLine(final Long id) {
        if (sectionDao.isExistingStation(id)) {
            throw new StationException("구간에 등록된 역은 삭제할 수 없습니다.");
        }
    }
}
