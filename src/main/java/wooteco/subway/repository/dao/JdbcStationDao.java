package wooteco.subway.repository.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.repository.entity.StationEntity;

@Repository
public class JdbcStationDao implements StationDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcStationDao(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
