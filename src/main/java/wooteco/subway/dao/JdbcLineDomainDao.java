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
import wooteco.subway.domain.LineDomain;
import wooteco.subway.domain.Name;
import wooteco.subway.domain.SectionsDomain;
import wooteco.subway.exception.line.NoSuchLineException;

@Repository
public class JdbcLineDomainDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final JdbcSectionDomainDao sectionDao;
    private final RowMapper<LineDomain> rowMapper = (resultSet, rowNumber) -> new LineDomain(
            resultSet.getLong("id"),
            new Name(resultSet.getString("name")),
            resultSet.getString("color")
    );

    public JdbcLineDomainDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource, final JdbcSectionDomainDao sectionDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
        this.sectionDao = sectionDao;
    }

    public Optional<LineDomain> insert(final LineDomain line) {
        try {
            final SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
            final long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
            final LineDomain lineDomain = new LineDomain(id, line.getName(), line.getColor(), line.getSections());
            return Optional.of(lineDomain);
        } catch (final DuplicateKeyException e) {
            return Optional.empty();
        }
    }
    public List<LineDomain> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, rowMapper)
                .stream()
                .map(it -> {
                    final SectionsDomain sections = sectionDao.findAllByLineId(it.getId());
                    return it.addSections(sections);
                })
                .collect(Collectors.toList());
    }

    public Optional<LineDomain> findById(final Long id) {
        try {
            final String sql = "SELECT * FROM line WHERE id = ?";
            final LineDomain line = jdbcTemplate.queryForObject(sql, rowMapper, id);
            final SectionsDomain sections = sectionDao.findAllByLineId(line.getId());
            return Optional.ofNullable(line.addSections(sections));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<LineDomain> updateById(final Long id, final LineDomain line) {
        try {
            final String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
            final int affectedRows = jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
            checkAffectedRows(affectedRows);
            return Optional.of(new LineDomain(id, line.getName(), line.getColor(), line.getSections()));
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
