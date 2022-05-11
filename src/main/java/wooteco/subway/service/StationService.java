package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.StationRequestDto;
import wooteco.subway.exception.CanNotDeleteException;
import wooteco.subway.exception.DuplicateStationNameException;
import wooteco.subway.repository.dao.JdbcSectionDao;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.StationEntity;

@Service
public class StationService {

    private final StationDao stationDao;
    // 인터페이스로 바꿔야함
    private final JdbcSectionDao sectionDao;

    public StationService(final StationDao stationDao, final JdbcSectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public Station register(final StationRequestDto stationRequestDto) {
        final Station station = new Station(stationRequestDto.getName());
        try {
            final StationEntity savedStationEntity = stationDao.save(new StationEntity(station));
            return new Station(savedStationEntity.getId(), savedStationEntity.getName());
        } catch (DuplicateKeyException exception) {
            throw new DuplicateStationNameException();
        }
    }

    public Station searchById(final Long id) {
        final StationEntity stationEntity = stationDao.findById(id);
        return new Station(stationEntity.getId(), stationEntity.getName());
    }

    public List<Station> searchAll() {
        return stationDao.findAll()
                .stream()
                .map(stationEntity -> new Station(stationEntity.getId(), stationEntity.getName()))
                .collect(Collectors.toList());
    }

    public void remove(final Long id) {
        if (sectionDao.findByStationId(id).size() != 0) {
            throw new CanNotDeleteException();
        }
        stationDao.deleteById(id);
    }
}
