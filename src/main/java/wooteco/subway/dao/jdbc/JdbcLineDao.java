package wooteco.subway.dao.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class JdbcLineDao implements LineDao {

    private static final RowMapper<Line> Line_ROW_MAPPER = (rs, rowNum) -> new Line(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color")
    );
    private final JdbcTemplate jdbcTemplate;

    public JdbcLineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line create(Line line) {
        String sql = "INSERT INTO line (name, color) VALUES(?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, line.getName());
            statement.setString(2, line.getColor());
            return statement;
        }, keyHolder);
        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    @Override
    public Line findById(Long id) {
        String sql = "SELECT * FROM line WHERE id=?";
        return jdbcTemplate.queryForObject(sql, Line_ROW_MAPPER, id);
    }

    @Override
    public List<Line> findAll() {
        String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, Line_ROW_MAPPER);
    }

    @Override
    public void update(Long id, String name, String color) {
        String sql = "UPDATE line SET name=?, color=? WHERE id=?";
        jdbcTemplate.update(sql, name, color, id);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM line WHERE id=?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existById(Long id) {
        String sql = "SELECT exists (select * FROM line WHERE id=?)";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != 0;
    }

    @Override
    public boolean existByName(String name) {
        String sql = "SELECT exists (select * FROM line WHERE name=?)";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != 0;
    }

    @Override
    public boolean existByColor(String color) {
        String sql = "SELECT exists (select * FROM line WHERE color=?)";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, color);
        return count != 0;
    }
}
