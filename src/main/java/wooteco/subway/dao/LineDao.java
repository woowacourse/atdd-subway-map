package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {
    private static final RowMapper<Line> LINE_ROW_MAPPER = (rs, rowNum) -> new Line(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color"),
            rs.getLong("upStationId"),
            rs.getLong("downStationId"),
            rs.getInt("distance"));

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Line line) {
        final String sql = "INSERT INTO line (name, color, upStationId, downStationId, distance) VALUES (?, ?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            preparedStatement.setLong(3, line.getUpStationId());
            preparedStatement.setLong(4, line.getDownStationId());
            preparedStatement.setInt(5, line.getDistance());
            return preparedStatement;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public boolean hasLine(String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM line WHERE name = ?);";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, name));
    }

    public boolean hasLine(Long id) {
        final String sql = "SELECT EXISTS (SELECT * FROM line WHERE id = ?);";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    public Line findById(Long id) {
        final String sql = "SELECT * FROM line WHERE id = ?;";
        return jdbcTemplate.queryForObject(sql, LINE_ROW_MAPPER, id);
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    public void update(Long id, Line line) {
        final String sql = "UPDATE line SET name = ?, color = ?, upStationId = ?, downStationId = ?, distance = ? WHERE id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getUpStationId(), line.getDownStationId(),
                line.getDistance(), id);
    }

    public void delete(Long id) {
        final String sql = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
