package wooteco.subway.line;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotExistItemException;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (resultSet, rowNumber) -> new Line(
        resultSet.getLong("id"),
        resultSet.getString("name"),
        resultSet.getString("color")
    );

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        String sql = "insert into LINE (name, color) values (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(con -> {
                PreparedStatement prepareStatement = con.prepareStatement(sql, new String[]{"id"});
                prepareStatement.setString(1, line.getName());
                prepareStatement.setString(2, line.getColor());
                return prepareStatement;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException();
        }

        return createNewObject(line, Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    private Line createNewObject(Line line, Long id) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        Objects.requireNonNull(field).setAccessible(true);
        ReflectionUtils.setField(field, line, id);
        return line;
    }

    public List<Line> findAll() {
        String sql = "select * from LINE";

        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line findById(Long id) {
        String sql = "select * from LINE where id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotExistItemException();
        }
    }

    public void update(Line newLine) {
        String sql = "update LINE set name = ?, color = ? where id = ?";
        try {
            jdbcTemplate.update(sql, newLine.getName(), newLine.getColor(), newLine.getId());
        } catch (DuplicateKeyException e) {
            throw new  DuplicateException();
        }
    }

    public void delete(Long id) {
        String sql = "delete from LINE where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
