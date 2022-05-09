package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.entity.StationEntity;
import wooteco.subway.domain.Station;

@Repository
public class StationDaoImpl implements StationDao{

    private final JdbcTemplate jdbcTemplate;

    public StationDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public StationEntity save(Station Station) {
        final String sql = "INSERT INTO station (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, Station.getName());
            return ps;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();

        return new StationEntity(id, Station.getName());
    }

    @Override
    public List<StationEntity> findAll() {
        final String sql = "SELECT id, name FROM station";
        return jdbcTemplate.query(sql, stationMapper());
    }

    private RowMapper<StationEntity> stationMapper() {
        return (resultSet, rowNum) -> new StationEntity(
            resultSet.getLong("id"),
            resultSet.getString("name")
        );
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM station where id = ?";
        int updateSize = jdbcTemplate.update(sql, id);
        return updateSize != 0;
    }

    @Override
    public boolean existsByName(String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM station WHERE name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }
}
