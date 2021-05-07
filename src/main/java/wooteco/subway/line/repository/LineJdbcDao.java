package wooteco.subway.line.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.Line;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class LineJdbcDao implements LineRepository {
    private final JdbcTemplate jdbcTemplate;

    public LineJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO line (name, color) VALUES (?, ?)";
        String name = line.getName();
        String color = line.getColor();
        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(query, new String[]{"id"});
            pstmt.setString(1, name);
            pstmt.setString(2, color);
            return pstmt;
        }, keyHolder);
        Long id = keyHolder.getKey().longValue();
        return new Line(id, name, color);
    }

    @Override
    public List<Line> findAll() {
        String query = "SELECT * FROM line";
        return jdbcTemplate.query(query, lineRowMapper());
    }

    private RowMapper<Line> lineRowMapper() {
        return (rs, rowNum) -> new Line(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("color")
        );
    }

    @Override
    public Optional<Line> findById(Long id) {
        String query = "SELECT * FROM line WHERE id = ?";
        List<Line> results = jdbcTemplate.query(query, lineRowMapper(), id);
        return results.stream()
                .findAny();
    }

    @Override
    public void updateById(Long id, Line updatedLine) {
        String query = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        String newName = updatedLine.getName();
        String newColor = updatedLine.getColor();
        jdbcTemplate.update(query, newName, newColor, id);
    }

    @Override
    public void deleteById(Long id) {
        String query = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(query, id);
    }
}
