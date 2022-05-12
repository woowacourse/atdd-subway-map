package wooteco.subway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public Section save(Section section) {
        final SqlParameterSource parameters = new BeanPropertySqlParameterSource(section);
        final Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Section(id, section.getUpStationId(), section.getDownStationId(), section.getLineId(),
                section.getDistance());
    }

    public void saveAll(List<Section> sections) {
        String SQL = "insert into SECTION (lineId, upStationId, downStationId, distance) "
                + "values (:lineId, :upStationId, :downStationId, :distance)";

        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(sections.toArray());
        namedParameterJdbcTemplate.batchUpdate(SQL, batch);
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

    public void deleteByLineId(Long lineId) {
        final String SQL = "delete from section where lineId = ?";
        jdbcTemplate.update(SQL, lineId);
    }
}
