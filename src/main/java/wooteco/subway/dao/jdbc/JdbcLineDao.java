package wooteco.subway.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcLineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Line> LINE_ROW_MAPPER = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color")
    );

    @Override
    public Line save(Line line) {
        String sql = "INSERT INTO line (name, color) VALUES(?, ?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            final PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, line.getName());
            statement.setString(2, line.getColor());
            return statement;
        }, keyHolder);

        final long id = keyHolder.getKey().longValue();

        return new Line(id, line.getName(), line.getColor());
    }

    @Override
    public Line findById(Long id) {
        String sql = "SELECT * FROM line WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, LINE_ROW_MAPPER, id);
    }

    @Override
    public List<Line> findAll() {
        String sql = "SELECT * FROM line";

        return jdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    @Override
    public Long updateByLine(Line line) {
        String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";

        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
        return line.getId();
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM line WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
