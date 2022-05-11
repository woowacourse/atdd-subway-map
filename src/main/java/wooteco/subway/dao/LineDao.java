package wooteco.subway.dao;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {

    private static final RowMapper<Line> LINE_ROW_MAPPER = (rs, rowNum) -> new Line(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color")
    );

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        var sql = "INSERT INTO line (name, color) VALUES(?, ?)";
        var keyHolder = new GeneratedKeyHolder();
        save(line, sql, keyHolder);
        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    private void save(Line line, String sql, KeyHolder keyHolder) {
        try {
            jdbcTemplate.update(connection -> {
                var statement = connection.prepareStatement(sql, new String[]{"id"});
                statement.setString(1, line.getName());
                statement.setString(2, line.getColor());
                return statement;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 노선 정보 입니다.");
        }
    }

    public Line findById(Long id) {
        var sql = "SELECT * FROM line WHERE id=?";

        try {
            return jdbcTemplate.queryForObject(sql, LINE_ROW_MAPPER, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("[ERROR] 해당 노선이 존재하지 않습니다.");
        }
    }

    public List<Line> findAll() {
        var sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    public void update(Long id, String name, String color) {
        var sql = "UPDATE line SET name=?, color=? WHERE id=?";
        var updatedRow = 0;
        try {
            updatedRow = jdbcTemplate.update(sql, name, color, id);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 노선 정보 입니다.");
        }

        checkUpdatedRow(updatedRow);
    }

    private void checkUpdatedRow(int updatedRow) {
        if (updatedRow == 0) {
            throw new NoSuchElementException("[ERROR] 해당 노선이 존재하지 않습니다.");
        }
    }

    public void deleteById(Long id) {
        var sql = "DELETE FROM line WHERE id=?";
        var deletedRow = jdbcTemplate.update(sql, id);

        checkUpdatedRow(deletedRow);
    }
}
