package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.line.domain.Section;
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

    @Transactional
    public Station save(Station station) {
        validateDuplicate(station.name());
        return stationDao.save(station);
    }

    private void validateDuplicate(final String name) {
        if (stationDao.findByName(name).isPresent()) {
            throw new IllegalStateException("[ERROR] 이미 존재하는 역입니다.");
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Station findById(Long id) {
        return stationDao.findById(id).orElseThrow(() ->
                new IllegalArgumentException("[ERROR] 존재하지 않는 역입니다."));
    }

    @Transactional
    public void delete(Long id) {
        List<Section> sections = sectionDao.findByStationId(id);
        if (!sections.isEmpty()) {
            throw new IllegalStateException("[ERROR] 구간에 역이 등록되어 삭제할 수 없습니다.");
        }
        stationDao.delete(id);
    }
}
