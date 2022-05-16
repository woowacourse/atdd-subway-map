package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.StationRequestDto;
import wooteco.subway.exception.CanNotDeleteException;
import wooteco.subway.exception.DuplicateStationNameException;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.StationEntity;

@Service
public class StationService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationService(final StationDao stationDao, final SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public Station register(final StationRequestDto stationRequestDto) {
        final Station station = Station.ofNullId(stationRequestDto.getName());
        try {
            return stationDao.save(new StationEntity(station)).createStation();
        } catch (DuplicateKeyException exception) {
            throw new DuplicateStationNameException();
        }
    }

    // 이거 문제 없는지 체크
    public Station searchById(final Long id) {
        try {
            return stationDao.findById(id).createStation();
        } catch (EmptyResultDataAccessException exception) {
            throw new NoSuchElementException("[ERROR] 역을 찾을 수 없습니다.");
        }
    }

    public List<Station> searchAll() {
        return stationDao.findAll()
                .stream()
                .map(StationEntity::createStation)
                .collect(Collectors.toList());
    }

    public void remove(final Long id) {
        if (sectionDao.findByStationId(id).size() != 0) {
            throw new CanNotDeleteException();
        }
        stationDao.deleteById(id);
    }
}
