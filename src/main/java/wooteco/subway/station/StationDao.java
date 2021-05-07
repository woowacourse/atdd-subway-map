package wooteco.subway.station;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.List;

public class StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final KeyHolder keyHolder = new GeneratedKeyHolder();

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(final String name) {
        final String sql = "INSERT INTO STATION (name) VALUES (?)";
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, name);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public boolean isDuplicatedName(final String name) {
        final String sql = "SELECT EXISTS(SELECT * FROM STATION WHERE name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM STATION";
        return jdbcTemplate.query(
                sql,
                (rs, rn) -> new Station(
                        rs.getLong("id"),
                        rs.getString("name")
                )
        );
    }

    public void delete(final Long id) {
        String sql = "DELETE FROM STATION WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Station findById(final Long id) {
        try {
            final String sql = "SELECT * FROM STATION WHERE id = ?";
            return jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> new Station(
                            rs.getLong("id"),
                            rs.getString("name")
                    ),
                    id);
        } catch (Exception e) {
            throw new StationException("존재하지 않는 역입니다.");
        }
    }
}
