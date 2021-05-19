package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.exception.StationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StationService {
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationService(final StationDao stationDao, final SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public StationResponse save(final StationRequest stationRequest) {
        validateName(stationRequest.getName());

        final Long id = stationDao.save(stationRequest.getName());
        return findById(id);
    }

    public void delete(final Long id) {
        if (stationDao.isNotExist(id)) {
            throw new StationException("존재하지 않는 역입니다.");
        }
        checkIsNotInLine(id);
        stationDao.delete(id);
    }

    public StationResponse findById(final Long id) {
        return new StationResponse(stationDao.findById(id));
    }

    public List<StationResponse> findAll() {
        final List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public List<StationResponse> idsToStations(final List<Long> stationIds) {
        return stationIds.stream()
                .map(this::findById)
                .collect(Collectors.toList());
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
