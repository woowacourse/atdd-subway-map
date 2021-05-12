package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {

    private JdbcTemplate jdbcTemplate;
    private RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
        new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color"));

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long create(String name, String color) {
        String createLineSql = "INSERT INTO line (name, color) VALUES (?, ?)";
        KeyHolder lineKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(createLineSql, new String[]{"id"});
            ps.setString(1, name);
            ps.setString(2, color);
            return ps;
        }, lineKeyHolder);
        return lineKeyHolder.getKey().longValue();
    }

    public List<Line> findAll() {
        String query = "SELECT * FROM line";
        return jdbcTemplate.query(query, lineRowMapper);
    }

    public Optional<Line> findById(Long lineId) {
        String query = "SELECT * FROM line WHERE id = ?";
        Line result = DataAccessUtils.singleResult(
            jdbcTemplate.query(query, lineRowMapper,
                lineId));
        return Optional.ofNullable(result);
    }

    public Optional<Line> findByName(String name) {
        String query = "SELECT * FROM line WHERE name = ?";
        Line result = DataAccessUtils.singleResult(
            jdbcTemplate.query(query, lineRowMapper, name));
        return Optional.ofNullable(result);
    }

    public int edit(Long lineId, String name, String color) {
        String query = "UPDATE line SET color = ?, name = ? WHERE id = ?";
        return jdbcTemplate.update(query, color, name, lineId);
    }

    public int deleteById(Long lineId) {
        String deleteLineQuery = "DELETE FROM line WHERE id = ?";
        return jdbcTemplate.update(deleteLineQuery, lineId);
    }
}
