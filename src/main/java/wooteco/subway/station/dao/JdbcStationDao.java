package wooteco.subway.station.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JdbcStationDao implements StationDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Station create(Station station) {
        String createSql = "INSERT INTO station (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(createSql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    @Override
    public List<Station> findAll() {
        String readAllSql = "SELECT * FROM station";
        return this.jdbcTemplate.query(readAllSql, stationRowMapper());
    }

    @Override
    public Station findById(Long id) {
        String readByIdSql = "SELECT * FROM station WHERE id = ?";
        return this.jdbcTemplate.queryForObject(readByIdSql, stationRowMapper(), id);
    }

    @Override
    public boolean existById(Long id) {
        String countByIdSql = "SELECT count(id) FROM station WHERE id = ?";
        int count = this.jdbcTemplate.queryForObject(countByIdSql, int.class, id);
        return count > 0;
    }

    @Override
    public boolean existByName(String name) {
        String countByNameSql = "SELECT count(id) FROM station WHERE name = ?";
        int count = this.jdbcTemplate.queryForObject(countByNameSql, int.class, name);
        return count > 0;
    }

    @Override
    public void removeById(Long id) {
        String deleteByIdSql = "DELETE FROM station WHERE id = ?";
        this.jdbcTemplate.update(deleteByIdSql, id);
    }

    private RowMapper<Station> stationRowMapper() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            return new Station(id, name);
        };
    }

}
