package wooteco.subway.repository.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.repository.entity.StationEntity;

public class JdbcStationDao implements StationDao {

    @Override
    public StationEntity save(final StationEntity stationEntity) {
        return null;
    }

    @Override
    public List<StationEntity> findAll() {
        return null;
    }

    @Override
    public Optional<StationEntity> findByName(final String name) {
        return Optional.empty();
    }

    @Override
    public Optional<StationEntity> findById(final Long id) {
        return Optional.empty();
    }

    @Override
    public void deleteById(final Long id) {

    }
}
