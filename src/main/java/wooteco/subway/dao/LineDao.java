package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color")
    );

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        final String sql = "insert into Line(name, color) values (?, ?)";
        if (isContainsLine(line)) {
            throw new IllegalStateException("노선 이름은 중복될 수 없습니다.");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return new Line(
                Objects.requireNonNull(keyHolder.getKey()).longValue(),
                line.getName(),
                line.getColor()
        );
    }

    public List<Line> findAll() {
        final String sql = "select id, name, color from Line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    private boolean isContainsLine(Line line) {
        final String sql = "select count(*) from Line where name = ? or color = ?";
        final int count = jdbcTemplate.queryForObject(sql, Integer.class, line.getName(), line.getColor());
        return count > 0;
    }

    public Line findById(Long id) {
        final String sql = "select id, name, color from Line where id = ?";
        if (!isExistById(id)) {
            throw new NoSuchElementException("해당하는 노선이 존재하지 않습니다.");
        }
        return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
    }

    private boolean isExistById(Long id) {
        final String sql = "select count(*) from Line where id = ?";
        final int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count > 0;
    }

    public void update(Long id, Line line) {
        final String sql = "update Line set name = ?, color = ? where id = ?";
        if (!isExistById(id)) {
            throw new NoSuchElementException("해당하는 노선이 존재하지 않습니다.");
        }
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    public void deleteById(Long id) {
        final String sql = "delete from Line where id = ?";
        if (!isExistById(id)) {
            throw new NoSuchElementException("해당하는 노선이 존재하지 않습니다.");
        }
        jdbcTemplate.update(sql, id);
    }
}
