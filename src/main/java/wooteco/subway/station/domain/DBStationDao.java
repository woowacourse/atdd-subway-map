package wooteco.subway.station.domain;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class DBStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    public DBStationDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    static RowMapper<Station> stationsMapper = (rs, rowNum) -> new Station(
            rs.getLong("id"),
            rs.getString("name")
    );

    @Override
    public Station save(final Station station) {
        validateDuplicate(station);
        String sql = "INSERT INTO station(name) values(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    @Override
    public List<Station> findAll() {
        return jdbcTemplate.query("select * from station order by id desc",
                stationsMapper);
    }

    @Override
    public Optional<Station> findById(final Long id) {
        List<Station> stations = jdbcTemplate.query("select * from station where id = ?",
                stationsMapper,
                id);

        if (stations.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(stations.get(0));
    }

    @Override
    public Optional<Station> findByName(final String name) {
        List<Station> stations = jdbcTemplate.query("select * from station where name = ?",
                stationsMapper,
                name);

        if (stations.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(stations.get(0));
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("디비 전체 삭제는 불가능!");
    }

    @Override
    public void delete(final Long id) {
        int rowCount = jdbcTemplate.update("delete from station where id = ?", id);
        if (rowCount == 0) {
            throw new IllegalStateException("없는 역입!");
        }
    }

    private void validateDuplicate(final Station station) {
        if (findByName(station.getName()).isPresent()) {
            throw new IllegalStateException("이미 있는 역임!");
        }
    }
}
