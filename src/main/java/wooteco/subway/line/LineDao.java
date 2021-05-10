package wooteco.subway.line;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicatedNameException;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> new Line(
        resultSet.getLong("id"),
        resultSet.getString("name"),
        resultSet.getString("color")
    );

    public LineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(final Line line) {
        try {
            final String sql = "INSERT INTO line (name, color) VALUES (?, ?)";
            final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            final PreparedStatementCreator preparedStatementCreator = con -> {
                final PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, line.getName());
                preparedStatement.setString(2, line.getColor());
                return preparedStatement;
            };
            jdbcTemplate.update(preparedStatementCreator, keyHolder);
            final long id = keyHolder.getKey().longValue();
            return findById(id).get();
        } catch (DuplicateKeyException e) {
            throw new DuplicatedNameException("중복된 이름의 노선입니다.");
        }
    }

    public void deleteById(final long id) {
        final String sql = "DELETE FROM line WHERE id = ?";
        int deletedCnt = jdbcTemplate.update(sql, id);

        if (deletedCnt < 1) {
            throw new DataNotFoundException("해당 Id의 노선이 없습니다.");
        }
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Optional<Line> findById(final Long id) {
        final String sql = "SELECT * FROM line WHERE id = ?";
        final List<Line> lines = jdbcTemplate.query(sql, lineRowMapper, id);
        return Optional.ofNullable(DataAccessUtils.singleResult(lines));
    }

    public Optional<Line> findByName(final String name) {
        final String sql = "SELECT * FROM line WHERE name = ?";
        final List<Line> lines = jdbcTemplate.query(sql, lineRowMapper, name);
        return Optional.ofNullable(DataAccessUtils.singleResult(lines));
    }

    public void update(final Line updatedLine) {
        final String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        int updatedCnt = jdbcTemplate.update(
            sql, updatedLine.getName(), updatedLine.getColor(), updatedLine.getId()
        );

        if (updatedCnt < 1) {
            throw new DataNotFoundException("해당 Id의 노선이 없습니다.");
        }
    }
}
