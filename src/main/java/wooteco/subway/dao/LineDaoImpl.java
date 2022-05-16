package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDaoImpl implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Line line) {
        final String sql = "INSERT INTO line (name, color) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, lineMapper());
    }

    private RowMapper<Line> lineMapper() {
        return (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color")
        );
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM line where id = ?";
        int updateSize = jdbcTemplate.update(sql, id);
        return updateSize != 0;
    }

    @Override
    public Optional<Line> findById(Long id) {
        final String sql = "SELECT * FROM line where id = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, lineMapper(), id));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public boolean updateById(Long id, Line line) {
        final String sql = "UPDATE line SET name = ?, color = ? where id = ?";
        int updateSize = jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
        return updateSize != 0;
    }

    @Override
    public boolean existsByName(String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM line WHERE name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }
}
