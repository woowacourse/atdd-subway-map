package wooteco.subway.dao;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class SectionDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Section save(long lineId, long upStationId, long downStationId, int distance) {
        Section section = new Section(lineId, upStationId, downStationId, distance);
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(section);
        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Section(id, lineId, upStationId, downStationId, distance);
    }
}
