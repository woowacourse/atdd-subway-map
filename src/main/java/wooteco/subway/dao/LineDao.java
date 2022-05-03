package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
            new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color")
            );

    public Line save(String name, String color) {
        final String sql = "INSERT INTO LINE(name,color) VALUES (?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, name);
            ps.setString(2, color);
            return ps;
        }, keyHolder);

        return new Line(keyHolder.getKey().longValue(), name, color);
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line findById(Long id) {
        final String sql = "SELECT * FROM LINE WHERE id = ?";
        Line line = jdbcTemplate.queryForObject(sql, lineRowMapper, id);
        if (line == null) {
            new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
        return line;
    }

    public void delete(Long id) {
        final String sql = "DELETE FROM LINE WHERE id = ?";
        int count = jdbcTemplate.update(sql, id);
        if (count == 0) {
            new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
    }

    public void update(Long id, String name, String color) {
        final String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        int count = jdbcTemplate.update(sql, name, color, id);
        if (count == 0) {
            new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
    }

    public void validate(String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE name = ?)";
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, name);
        if (result) {
            throw new IllegalArgumentException("지하철 노선 이름이 중복될 수 없습니다.");
        }
    }

    public void validate(Long id, String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE id != ? AND name = ?)";
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, id, name);
        if (result) {
            throw new IllegalArgumentException("지하철 노선 이름이 중복될 수 없습니다.");
        }
    }

}
