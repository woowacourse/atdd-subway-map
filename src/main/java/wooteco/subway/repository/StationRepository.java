package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import wooteco.subway.dao.SequenceDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.exception.RowNotFoundException;

@Repository
public class StationRepository {

    private final StationDao stationDao;
    private final SequenceDao sequenceDao;

    public StationRepository(StationDao stationDao, SequenceDao sequenceDao) {
        this.stationDao = stationDao;
        this.sequenceDao = sequenceDao;
    }

    public Station findById(Long id) {
        final StationEntity entity = stationDao.findById(id)
            .orElseThrow(() -> new RowNotFoundException(String.format("%d의 id를 가진 역이 존재하지 않습니다.", id)));
        final Station station = new Station(entity.getId(), entity.getName());
        setSequenceIfPresent(station);
        return station;
    }

    private void setSequenceIfPresent(Station station) {
        // sequenceDao.findByStationId(station.getId())
        //     .ifPresent(entity -> station.setSequence(new Sequence(entity.getSequence())));
    }

    public List<Station> findAllStations() {
        return stationDao.findAll()
            .stream()
            .map(entity -> new Station(entity.getId(), entity.getName()))
            .collect(Collectors.toList());
    }

    public Station create(Station station) {
        final StationEntity saved = stationDao.save(StationEntity.from(station));
        return new Station(saved.getId(), saved.getName());
    }

    public void deleteById(Long id) {
        final boolean isDeleted = stationDao.deleteById(id);
        if (!isDeleted) {
            throw new RowNotFoundException("삭제하고자 하는 역이 존재하지 않습니다.");
        }
    }
}
