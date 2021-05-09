package wooteco.subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color"),
            Collections.emptyList()
    );

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(String lineName, String lineColor) {
        String sql = "INSERT INTO LINE (name, color) values (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, lineName);
            ps.setString(2, lineColor);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<Line> findAll() {
        String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public List<Long> findStationsIdByLineId(long id) {
        String sql = "SELECT DISTINCT STATION.id AS station_id FROM STATION JOIN SECTION ON SECTION.line_id = ? " +
                "WHERE SECTION.up_station_id = STATION.id OR SECTION.down_station_Id = STATION.id";

        return jdbcTemplate.queryForList(sql, Long.class, id);
    }

    public Line findById(long id) {
        String sql = "SELECT * FROM LINE WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
    }

    public void update(long id, String lineName, String lineColor) {
        String sql = "UPDATE LINE set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, lineName, lineColor, id);
    }

    public void delete(long id) {
        String sql = "DELETE FROM LINE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
