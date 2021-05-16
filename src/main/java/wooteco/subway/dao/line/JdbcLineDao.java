package wooteco.subway.dao.line;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Line save(Line line) {
        String sql = "INSERT INTO line (name, color) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            return preparedStatement;
        }, keyHolder);
        return Line.of(keyHolder.getKey().longValue(), line.getName(), line.getColor(), line.getSections());
    }

    @Override
    public Optional<Line> findByNameOrColor(String name, String color) {
        String sql = "SELECT * FROM line WHERE (name = ? OR color = ?)";
        try {
            return Optional.ofNullable((Line) jdbcTemplate.queryForObject(sql, getRowMapper(), name, color));
        }
        catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Line> findById(Long lineId) {
        String sql = "SELECT * FROM line WHERE id = ?";
        try {
            return Optional.ofNullable((Line) jdbcTemplate.queryForObject(sql, getRowMapper(), lineId));
        }
        catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Line> findAll() {
        String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    @Override
    public void update(Long id, String name, String color) {
        String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, name, color, id);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private RowMapper getRowMapper() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            String color = rs.getString("color");
            return Line.of(id, name, color);
        };
    }
}
