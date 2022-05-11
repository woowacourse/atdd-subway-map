package wooteco.subway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    public Section save(Section section) {
        final SqlParameterSource parameters = new BeanPropertySqlParameterSource(section);
        final Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Section(id, section.getUpStationId(), section.getDownStationId(), section.getLineId(),
                section.getDistance());
    }

    public List<Section> findAllByLineId(Long lineId) {
        final String SQL = "select * from section where lineId = ?";
        return jdbcTemplate.query(SQL, (rs, rowNum) ->
                new Section(
                        rs.getLong("id"),
                        rs.getLong("upStationId"),
                        rs.getLong("downStationId"),
                        rs.getLong("lineId"),
                        rs.getInt("distance")
                ), lineId);
    }
}
