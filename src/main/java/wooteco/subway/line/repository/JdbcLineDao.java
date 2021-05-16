package wooteco.subway.line.repository;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.Line;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class JdbcLineDao implements LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (rs, rowNum) -> new Line(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color")
    );

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
        return jdbcTemplate.query(query, lineRowMapper);
    }

    @Override
    public Line findById(Long id) {
        String query = "SELECT * FROM line WHERE id = ?";
        List<Line> results = jdbcTemplate.query(query, lineRowMapper, id);
        return DataAccessUtils.singleResult(results);
    }

    @Override
    public void update(Line updatedLine) {
        String query = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        String newName = updatedLine.getName();
        String newColor = updatedLine.getColor();
        jdbcTemplate.update(query, newName, newColor, updatedLine.getId());
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public boolean isExistingName(String name) {
        String query = "SELECT EXISTS (SELECT * FROM line WHERE name = (?))";
        return jdbcTemplate.queryForObject(query, Boolean.class, name);
    }

    @Override
    public boolean existNewNameExceptCurrentName(String newName, String oldName) {
        String query = "SELECT EXISTS (SELECT * FROM line WHERE name IN (?) AND name NOT IN (?))";
        return jdbcTemplate.queryForObject(query, Boolean.class, newName, oldName);
    }
}
