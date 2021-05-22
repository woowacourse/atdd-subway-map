package wooteco.subway.line;

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
import wooteco.subway.exception.SubwayCustomException;
import wooteco.subway.exception.SubwayException;

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
            throw new SubwayCustomException(SubwayException.DUPLICATE_LINE_EXCEPTION);
        }
        return new Line(Objects.requireNonNull(keyHolder.getKey()).longValue(), line.getName(),
            line.getColor());
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
            throw new SubwayCustomException(SubwayException.NOT_EXIST_LINE_EXCEPTION);
        }
    }

    public int update(Line newLine) {
        String sql = "update LINE set name = ?, color = ? where id = ?";
        try {
            return jdbcTemplate.update(sql, newLine.getName(), newLine.getColor(), newLine.getId());
        } catch (DuplicateKeyException e) {
            throw new SubwayCustomException(SubwayException.DUPLICATE_LINE_EXCEPTION);
        }
    }

    public void delete(Long id) {
        String sql = "delete from LINE where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public boolean isExistLine(Long lindId) {
        String sql = "select exists(select * from LINE where id = ?) as isLine";

        return Objects.requireNonNull(jdbcTemplate.queryForObject(sql, Boolean.class, lindId));
    }
}
