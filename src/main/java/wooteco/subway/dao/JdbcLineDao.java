package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Name;
import wooteco.subway.domain.Sections;
import wooteco.subway.exception.line.NoSuchLineException;

@Repository
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final SectionDao sectionDao;
    private final RowMapper<Line> rowMapper = (resultSet, rowNumber) -> new Line(
            resultSet.getLong("id"),
            new Name(resultSet.getString("name")),
            resultSet.getString("color")
    );

    public JdbcLineDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource, final SectionDao sectionDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
        this.sectionDao = sectionDao;
    }

    public Optional<Line> insert(final Line line) {
        try {
            final SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
            final long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
            return Optional.of(new Line(id, line.getName(), line.getColor()));
        } catch (final DuplicateKeyException e) {
            return Optional.empty();
        }
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, rowMapper)
                .stream()
                .map(it -> {
                    final Sections sections = sectionDao.findAllByLineId(it.getId());
                    return it.addSections(sections);
                })
                .collect(Collectors.toList());
    }

    public Optional<Line> findById(final Long id) {
        try {
            final String sql = "SELECT * FROM line WHERE id = ?";
            final Line line = jdbcTemplate.queryForObject(sql, rowMapper, id);
            final Sections sections = sectionDao.findAllByLineId(id);
            return Optional.ofNullable(line.addSections(sections));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Line> updateById(final Long id, final Line line) {
        try {
            final String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
            final int affectedRows = jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
            checkAffectedRows(affectedRows);
            return Optional.of(new Line(id, new Name(line.getName()), line.getColor(), line.getSections()));
        } catch (final DuplicateKeyException e) {
            return Optional.empty();
        }
    }

    private void checkAffectedRows(final int affectedRows) {
        if (affectedRows == 0) {
            throw new NoSuchLineException();
        }
    }

    public Integer deleteById(final Long id) {
        final String sql = "DELETE FROM line WHERE id = ?";
        final int affectedRows = jdbcTemplate.update(sql, id);
        checkAffectedRows(affectedRows);
        return affectedRows;
    }
}
