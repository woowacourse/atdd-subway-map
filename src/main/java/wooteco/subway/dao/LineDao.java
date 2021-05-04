package wooteco.subway.dao;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class LineDao {
    private JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
            new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color"));

    public Long save(Line line) {
        String sql = "INSERT INTO LINE (name, color) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public List<Line> findAll() {
        String query = "SELECT * FROM LINE";
        return jdbcTemplate.query(query, lineRowMapper);
    }

    public Optional<Line> findById(Long id) {
        String query = "SELECT * FROM LINE WHERE id = ?";
        Line result = DataAccessUtils.singleResult(
                jdbcTemplate.query(query, lineRowMapper, id)
        );
        return Optional.ofNullable(result);
    }

    public Optional<Line> findByName(String name) {
        String query = "SELECT * FROM LINE WHERE name = ?";
        Line result = DataAccessUtils.singleResult(
                jdbcTemplate.query(query, lineRowMapper, name)
        );
        return Optional.ofNullable(result);
    }

    public int update(Long id, String color, String name) {
        String query = "UPDATE LINE SET color = ?, name = ? WHERE id = ?";
        return jdbcTemplate.update(query, color, name, id);
    }

    public int deleteById(Long id) {
        String query = "DELETE FROM LINE WHERE id = ?";
        return jdbcTemplate.update(query, id);
    }
}
