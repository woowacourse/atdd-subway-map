package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.entity.StationEntity;

@Repository
public class JdbcStationDao implements StationDao {

    public static final RowMapper<StationEntity> STATION_ROW_MAPPER = (resultSet, rowNum) -> new StationEntity(
        resultSet.getLong("id"),
        resultSet.getString("name")
    );
    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;

    public JdbcStationDao(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("station")
            .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long update(StationEntity entity) {
        final String sql = "UPDATE station SET name = ? WHERE id = ?";
        final int updatedCount = jdbcTemplate.update(sql, entity.getName(), entity.getId());
        if (!isUpdated(updatedCount)) {
            return null;
        }
        return entity.getId();
    }

    @Override
    public Long save(StationEntity station) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(station);
        return jdbcInsert.executeAndReturnKey(param).longValue();
    }

    @Override
    public List<StationEntity> findAll() {
        final String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, STATION_ROW_MAPPER);
    }

    @Override
    public Long delete(Long id) {
        final String sql = "DELETE FROM station WHERE id = ?";
        final int deletedCount = jdbcTemplate.update(sql, id);
        if (!isUpdated(deletedCount)) {
            return null;
        }
        return id;
    }

    private boolean isUpdated(int updatedCount) {
        return updatedCount != 0;
    }

    @Override
    public Optional<StationEntity> findById(Long id) {
        final String sql = "SELECT * FROM station WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, STATION_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
