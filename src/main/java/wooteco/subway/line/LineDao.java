package wooteco.subway.line;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicatedFieldException;

@Repository
public class LineDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> new Line(
        resultSet.getLong("id"),
        resultSet.getString("name"),
        resultSet.getString("color")
    );

    public LineDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Line save(final Line line) {
        try {
            final String sql = "INSERT INTO line (name, color) VALUES (:name, :color)";
            final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            final SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(line);
            namedParameterJdbcTemplate.update(sql, sqlParameterSource, keyHolder);
            final long id = keyHolder.getKey().longValue();
            return findById(id).orElseThrow(() -> new DataNotFoundException("해당 노선을 찾을 수 없습니다."));
        } catch (DuplicateKeyException e) {
            throw new DuplicatedFieldException("중복된 이름의 노선입니다.");
        }
    }

    public void deleteById(final long id) {
        final String sql = "DELETE FROM line WHERE id = :id";

        int deletedCnt = namedParameterJdbcTemplate.update(
            sql, Collections.singletonMap("id", id)
        );

        if (deletedCnt < 1) {
            throw new DataNotFoundException("해당 Id의 노선이 없습니다.");
        }
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return namedParameterJdbcTemplate.query(sql, lineRowMapper);
    }

    public Optional<Line> findById(final Long id) {
        final String sql = "SELECT * FROM line WHERE id = :id";
        final List<Line> lines = namedParameterJdbcTemplate.query(
            sql, Collections.singletonMap("id", id), lineRowMapper
        );
        return Optional.ofNullable(DataAccessUtils.singleResult(lines));
    }

    public Optional<Line> findByName(final String name) {
        final String sql = "SELECT * FROM line WHERE name = :name";
        final List<Line> lines = namedParameterJdbcTemplate.query(
            sql, Collections.singletonMap("name", name), lineRowMapper
        );
        return Optional.ofNullable(DataAccessUtils.singleResult(lines));
    }

    public void update(final Line updatedLine) {
        final String sql = "UPDATE line SET name = :name, color = :color WHERE id = :id";
        final SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(updatedLine);
        final int updateCount = namedParameterJdbcTemplate.update(sql, sqlParameterSource);

        if (updateCount < 1) {
            throw new DataNotFoundException("해당 Id의 노선이 없습니다.");
        }
    }
}
