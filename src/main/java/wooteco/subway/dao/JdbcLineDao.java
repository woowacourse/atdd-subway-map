package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color")
    );

    public JdbcLineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(Line line) {
        final String sql = "INSERT INTO LINE (name, color) VALUES (?, ?)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);
        final Long newLineId = keyHolder.getKey().longValue();

        return new Line(newLineId, line.getName(), line.getColor());
    }

    @Override
    public Line findById(Long id) {
        try {
            final String sql = "SELECT * FROM LINE WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT * from LINE";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    @Override
    public boolean hasLine(String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    @Override
    public void updateById(Long id, String name, String color) {
        final String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, name, color, id);
    }

    @Override
    public void deleteById(Long id) {
        final String sql = "DELETE FROM LINE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
