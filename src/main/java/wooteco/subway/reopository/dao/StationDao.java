package wooteco.subway.reopository.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.reopository.Entity.StationEntity;

@Repository
public class StationDao {

    private static final RowMapper<StationEntity> mapper = (rs, rowNum) ->
            new StationEntity(
                    rs.getLong("id"),
                    rs.getString("name")
            );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    public Long save(StationEntity station) {
        SqlParameterSource parameters = new MapSqlParameterSource("name", station.getName());
        return simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
    }

    public Optional<StationEntity> findById(Long id) {
        String sql = "select * from station where id = ?";
        return Optional.ofNullable(DataAccessUtils.singleResult(jdbcTemplate.query(sql, mapper, id)));
    }

    public List<StationEntity> findAll() {
        String sql = "select * from station";
        return jdbcTemplate.query(sql, mapper);
    }

    public void deleteById(Long id) {
        String sql = "delete from station where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public boolean existByName(String name) {
        String sql = "select exists (select name from station where name = ? limit 1) as success";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }
}

