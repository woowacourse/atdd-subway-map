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
public class LineDaoImpl implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Line> actorRowMapper = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color")
    );

    @Override
    public Line save(Line line) {
        String sql = "insert into line (name, color) values (?, ?) ";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Line(id, line.getName(), line.getColor());
    }

    @Override
    public List<Line> findAll() {
        String sql = "select * from line";
        return jdbcTemplate.query(sql, actorRowMapper);
    }

    @Override
    public int deleteById(Long id) {
        String sql = "delete from line where id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public Line findById(Long id) {
        String sql = "select * from line where id = ?";
        return jdbcTemplate.queryForObject(sql, actorRowMapper, id);
    }

    @Override
    public boolean exists(Line line) {
        String sql = "select exists (select name from line where name = ? or color = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, line.getName(), line.getColor());
    }

    @Override
    public boolean exists(final Long id) {
        String sql = "select exists (select id from line where id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    @Override
    public void update(Long id, Line updatingLine) {
        String sql = "update line set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, updatingLine.getName(), updatingLine.getColor(), id);
    }
}
