package wooteco.subway.line;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Line> lineRowMapper() {
        return (resultSet, rowNum) -> new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color")
        );
    }

    public Long save(String name, String color) {
        String sql = "insert into Line (name, color) values (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, name);
            ps.setString(2, color);
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey())
                      .longValue();
    }

    public Optional<Line> findById(Long id) throws IncorrectResultSizeDataAccessException {
        String sql = "select id, name, color from LINE where id = ?";
        List<Line> result = jdbcTemplate.query(sql, lineRowMapper(), id);
        return Optional.ofNullable(DataAccessUtils.singleResult(result));
    }

    public Optional<Line> findByName(String name) throws IncorrectResultSizeDataAccessException {
        String sql = "select id, name, color from LINE where name = ?";
        List<Line> result = jdbcTemplate.query(sql, lineRowMapper(), name);
        return Optional.ofNullable(DataAccessUtils.singleResult(result));
    }

    public List<Line> findAll() {
        String sql = "select id, name, color from LINE";
        return jdbcTemplate.query(sql, lineRowMapper());
    }

    public void update(Long id, String name, String color) {
        String sql = "update LINE set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, name, color, id);
    }

    public void delete(Long id) {
        String sql = "delete from LINE where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
