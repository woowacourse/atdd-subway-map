package wooteco.subway.line;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.DuplicateLineNameException;
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
        String sql = "INSERT INTO LINE (name, color) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(con -> {
                PreparedStatement prepareStatement = con.prepareStatement(sql, new String[]{"id"});
                prepareStatement.setString(1, line.getName());
                prepareStatement.setString(2, line.getColor());
                return prepareStatement;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new DuplicateLineNameException(e);
        }

        return createNewObject(line, keyHolder.getKey().longValue());
    }

    private Line createNewObject(Line line, Long id) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, id);
        return line;
    }

    public List<Line> findAll() {
        String sql = "SELECT * FROM LINE";

        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line findById(Long id) {
        String sql = "SELECT * FROM LINE WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotExistItemException(e);
        }
    }

    public Line update(Line newLine) {
        String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, newLine.getName(), newLine.getColor(), newLine.getId());
        return newLine;
    }

    public void delete(Long id) {
        String sql = "DELETE FROM LINE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

}
