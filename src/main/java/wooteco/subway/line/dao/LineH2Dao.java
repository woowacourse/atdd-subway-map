package wooteco.subway.line.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class LineH2Dao implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
            Line.of(resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color"));

    public LineH2Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(Line line) {
        String insertQuery = "INSERT INTO line(name, color) VALUES(?, ?);";
        jdbcTemplate.update(insertQuery, line.getName(), line.getColor());

        String findQuery = "SELECT * FROM line WHERE name = ?;";
        return jdbcTemplate.queryForObject(findQuery, lineRowMapper, line.getName());
    }

    @Override
    public Optional<Line> findById(Long id) {
        String findQuery = "SELECT * FROM line WHERE id = ?;";
        return jdbcTemplate.query(findQuery, lineRowMapper, id).stream().findAny();
    }

    @Override
    public Optional<Line> findByName(String lineName) {
        String findQuery = "SELECT * FROM line WHERE name = ?;";
        return jdbcTemplate.query(findQuery, lineRowMapper, lineName).stream().findAny();
    }

    @Override
    public List<Line> findAll() {
        String findQuery = "SELECT * FROM line;";
        return jdbcTemplate.query(findQuery, lineRowMapper);
    }

    @Override
    public void delete(Long id) {
        String findQuery = "DELETE FROM line WHERE id = ?;";
        jdbcTemplate.update(findQuery, id);
    }

    @Override
    public void update(Line newLine) {
        String updateQuery = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, newLine.getName(), newLine.getColor(), newLine.getId());
    }
}
