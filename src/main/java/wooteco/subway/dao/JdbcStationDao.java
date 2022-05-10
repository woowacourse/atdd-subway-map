package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao implements StationDao {

    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;

    public JdbcStationDao(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("station")
            .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Station> save(Station station) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(station);
        try {
            final Long id = jdbcInsert.executeAndReturnKey(param).longValue();
            return Optional.of(new Station(id, station.getName()));
        } catch (DuplicateKeyException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Station> findAll() {
        final String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
        ));
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM station WHERE id = ?";
        final int deletedCount = jdbcTemplate.update(sql, id);
        return isUpdated(deletedCount);
    }

    private boolean isUpdated(int updatedCount) {
        return updatedCount == 1;
    }
}
