package wooteco.subway.station.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import wooteco.subway.station.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

public class StationJdbcDao implements StationRepository {

    private final JdbcTemplate jdbcTemplate;

    public StationJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO station (name) VALUES (?)";
        String name = station.getName();
        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(query, new String[]{"id"});
            pstmt.setString(1, name);
            return pstmt;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Station(id, name);
    }

    @Override
    public List<Station> findAll() {
        String query = "SELECT * FROM station";
        return jdbcTemplate.query(query, stationRowMapper());
    }

    @Override
    public boolean validateDuplicateName(String name) {
        String query = "SELECT COUNT(*) FROM station WHERE name = (?)";
        return jdbcTemplate.queryForObject(query, Integer.class, name) > 0;
    }

    private RowMapper<Station> stationRowMapper() {
        return (rs, rowNum) -> new Station(
                rs.getLong("id"),
                rs.getString("name")
        );
    }

    @Override
    public void deleteById(Long id) {
        String query = "DELETE FROM station WHERE id = (?)";
        jdbcTemplate.update(query, id);
    }
}
