package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Service
@Transactional
public class StationService {
    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public StationService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    private void validateId(Long id) {
        if (!stationDao.isValidId(id)) {
            throw new EmptyResultDataAccessException(id.intValue());
        }
    }

    public void deleteById(Long id) {
        validateId(id);
        stationDao.deleteById(id);
        List<Long> totalLineIds = lineDao.findAll().stream()
                .map(Line::getId).collect(Collectors.toList());
        for (Long lineId : totalLineIds) {
            sectionDao.deleteByLineId(lineId);
        }
    }
}
