package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (rs, rowNum) -> new Line(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color")
    );

    public JdbcLineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(final Line line) {
        final String sql = "insert into LINE (name, color) values(?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Line(id, line.getName(), line.getColor());
    }

    @Override
    public Line findById(final Long id) {
        final String sql = "select * from LINE where id = ?";
        return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
    }

    @Override
    public List<Line> findAll() {
        final String sql = "select * from LINE";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    @Override
    public boolean existByName(final String name) {
        final String sql = "select exists (select * from LINE where name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    @Override
    public boolean existById(final Long id) {
        return false;
    }

    @Override
    public int update(final Line line) {
        final String sql = "update LINE set name = ?, color = ? where id = ?";
        return jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
    }

    @Override
    public int delete(final Long id) {
        final String sql = "delete from LINE where id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
