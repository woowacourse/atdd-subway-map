package wooteco.subway.line.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.line.Line;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class JdbcLineDao implements LineDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcLineDao(JdbcTemplate jdbcTemplate) {
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
    public Line findById(Long id) {
        try {
            String query = "SELECT * FROM line WHERE id = ?";
            return jdbcTemplate.queryForObject(query, lineRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new LineNotFoundException();
        }
    }

    @Override
    public void update(Line updatedLine) {
        String query = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        Long id = updatedLine.getId();
        String newName = updatedLine.getName();
        String newColor = updatedLine.getColor();
        jdbcTemplate.update(query, newName, newColor, id);
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public boolean existByName(String name) {
        String query = "SELECT EXISTS (SELECT * FROM line WHERE name = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, name);
    }

    @Override
    public boolean existByNameAndNotInOriginalName(String name, String originalName) {
        String query = "SELECT EXISTS (SELECT * FROM line WHERE name = ? AND name NOT IN (?))";
        return jdbcTemplate.queryForObject(query, Boolean.class, name, originalName);
    }
}
