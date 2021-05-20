package wooteco.subway.station.dao;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class StationDaoImpl implements StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationMapper;

    public StationDaoImpl(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.stationMapper = (rs, rowNum) -> new Station(
                rs.getLong("id"),
                rs.getString("name"));
    }

    @Override
    public Station save(final Station station) {
        String sql = "INSERT INTO STATION(name) values(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.name());
            return ps;
        }, keyHolder);
        long newId = keyHolder.getKey().longValue();
        return new Station(newId, station.name());
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * " +
                "FROM STATION " +
                "ORDER BY id DESC";
        return jdbcTemplate.query(sql, stationMapper);
    }

    @Override
    public Optional<Station> findById(final Long id) {
        String sql = "SELECT * " +
                "FROM STATION " +
                "WHERE id = ?";

        List<Station> stations = jdbcTemplate.query(sql, stationMapper, id);
        return Optional.ofNullable(DataAccessUtils.singleResult(stations));
    }

    @Override
    public Optional<Station> findByName(final String name) {
        String sql = "SELECT * " +
                "FROM STATION " +
                "WHERE name = ?";

        List<Station> stations = jdbcTemplate.query(sql, stationMapper, name);
        return Optional.ofNullable(DataAccessUtils.singleResult(stations));
    }

    @Override
    public void delete(final Long id) {
        String sql = "DELETE FROM STATION " +
                "WHERE id = ?";

        int rowCount = jdbcTemplate.update(sql, id);
        if (rowCount == 0) {
            throw new IllegalStateException("[ERROR] 존재하지 않는 id입니다.");
        }
    }
}
