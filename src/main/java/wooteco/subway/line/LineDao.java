package wooteco.subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color")
    );

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(String lineName, String lineColor) {
        String sql = "INSERT INTO LINE (name, color) values (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, lineName);
            ps.setString(2, lineColor);
            return ps;
        }, keyHolder);

        final long lineId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Line(lineId, lineName, lineColor);
    }

    public List<Line> findAll() {
        String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Optional<Line> findById(long id) {
        String sql = "SELECT * FROM LINE WHERE id = ?";
        final List<Line> result = jdbcTemplate.query(sql, lineRowMapper, id);
        return result.stream().findAny();
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
