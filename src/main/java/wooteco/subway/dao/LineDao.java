package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(final Line line) {
        final String sql = "INSERT INTO LINE (name, color) VALUES (?, ?)";

        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        final long lineId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Line(lineId, line.getName(), line.getColor());
    }

    public Optional<Line> findById(final Long id) {
        final String sql = "SELECT id, name, color FROM LINE WHERE id = ?";

        final List<Line> lines = jdbcTemplate.query(sql, (resultSet, rowNum) -> new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color")
        ), id);

        return Optional.ofNullable(DataAccessUtils.singleResult(lines));
    }

    public List<Line> findAll() {
        final String sql = "SELECT id, name, color FROM LINE";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color")
        ));
    }

    public void update(final Long id, final Line line) {
        final String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    public void deleteById(final Long id) {
        final String sql = "DELETE FROM LINE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public boolean existByName(final String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE NAME = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    public boolean existById(final Long id) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE ID = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }
}
