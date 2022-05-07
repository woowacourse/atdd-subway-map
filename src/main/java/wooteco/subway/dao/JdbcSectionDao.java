package wooteco.subway.dao;

import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final SimpleJdbcInsert simpleJdbcInsert;

    public JdbcSectionDao(final DataSource dataSource) {
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Long insert(final Section section) {
        final SqlParameterSource params = new BeanPropertySqlParameterSource(section);
        return simpleJdbcInsert.executeAndReturnKey(params).longValue();
    }
}
