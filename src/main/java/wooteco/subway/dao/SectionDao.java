package wooteco.subway.dao;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

import javax.sql.DataSource;

@Repository
public class SectionDao {

    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(DataSource dataSource) {
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    public void save(Section section) {
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(section);
        simpleJdbcInsert.execute(namedParameters);
    }
}
