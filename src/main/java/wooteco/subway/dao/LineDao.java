package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Line;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> new Line(
        resultSet.getLong("id"),
        resultSet.getString("name"),
        resultSet.getString("color")
    );

    public Line save(Line line) {
        final String sql = "INSERT INTO line (name, color) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[] {"id"});
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            return preparedStatement;
        }, keyHolder);

        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    public Optional<Line> findByName(String name) {
        final String sql = "SELECT * FROM line WHERE name = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, lineRowMapper, name));
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Line> findById(Long id) {
        final String sql = "SELECT * FROM line WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, lineRowMapper, id));
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public void update(Long id, Line line) {
        final String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    public void delete(Long id) {
        final String sql = "delete from line where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
