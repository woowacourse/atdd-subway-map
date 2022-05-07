package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import wooteco.subway.domain.Line;
import wooteco.subway.exception.RowDuplicatedException;
import wooteco.subway.exception.RowNotFoundException;

@Repository
public class JdbcLineDao implements LineDao {

    private static final RowMapper<Line> LINE_ROW_MAPPER = (resultSet, rowNum) -> new Line(
        resultSet.getLong("id"),
        resultSet.getString("name"),
        resultSet.getString("color")
    );

    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;

    public JdbcLineDao(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("line")
            .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(Line line) {
        try {
            final SqlParameterSource param = new BeanPropertySqlParameterSource(line);
            final Long id = jdbcInsert.executeAndReturnKey(param).longValue();
            return createNewObject(line, id);
        } catch (DuplicateKeyException ignored) {
            throw new RowDuplicatedException("이미 존재하는 노선 이름입니다.");
        }
    }

    private Line createNewObject(Line line, Long id) {
        final Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, id);
        return line;
    }

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    @Override
    public Line findById(Long id) {
        final String sql = "SELECT * FROM line WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, LINE_ROW_MAPPER, id);
        } catch (EmptyResultDataAccessException e) {
            throw new RowNotFoundException("조회하고자 하는 노선이 존재하지 않습니다.");
        }
    }

    @Override
    public void update(Line line) {
        final String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        final int updatedCount = jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
        validateUpdated(updatedCount);
    }

    private void validateUpdated(int updatedCount) {
        if (updatedCount == 0) {
            throw new RowNotFoundException("수정하고자 하는 노선이 존재하지 않습니다.");
        }
    }

    @Override
    public void delete(Long id) {
        final String sql = "DELETE FROM line WHERE id = ?";
        final int deletedCount = jdbcTemplate.update(sql, id);
        validateDeleted(deletedCount);
    }

    private void validateDeleted(int deletedCount) {
        if (deletedCount == 0) {
            throw new RowNotFoundException("삭제하고자 하는 노선이 존재하지 않습니다.");
        }
    }
}
