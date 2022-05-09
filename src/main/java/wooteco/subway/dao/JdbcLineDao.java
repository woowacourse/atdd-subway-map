package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.LineEntity;

@Repository
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcLineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public LineEntity save(LineEntity line) {
        String sql = "insert into line (name, color) values (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new LineEntity(id, line.getName(), line.getColor());
    }

    @Override
    public Optional<LineEntity> findById(Long id) {
        String sql = "select * from line where id = ?";

        try {
            LineEntity line = jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> createLine(rs), id);
            return Optional.ofNullable(line);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private LineEntity createLine(ResultSet rs) throws SQLException {
        return new LineEntity(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color")
        );
    }

    @Override
    public Optional<LineEntity> findByName(String name) {
        String sql = "select * from line where name = ?";

        try {
            LineEntity line = jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> createLine(rs), name);
            return Optional.ofNullable(line);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<LineEntity> findAll() {
        String sql = "select * from line";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createLine(rs));
    }

    @Override
    public void update(LineEntity line) {
        String sql = "update line set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
    }

    @Override
    public int deleteById(Long id) {
        String sql = "delete from line where id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
