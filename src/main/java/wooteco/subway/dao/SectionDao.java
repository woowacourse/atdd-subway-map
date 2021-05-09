package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.section.Section;

import java.util.List;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("ID");
    }

    public long save(Section section) {
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(section);
        return simpleJdbcInsert.executeAndReturnKey(sqlParameterSource)
                .longValue();
    }

    public List<Section> findAllByLineId(long lineId) {
        String query = "SELECT ID, DISTANCE FROM SECTION WHERE LINE_ID = ?";
        RowMapper<Section> sectionRowMapper = (resultSet, rowNumber) -> {
            long id = resultSet.getLong("ID");
            int distance = resultSet.getInt("DISTANCE");
            return new Section(id, distance, lineId);
        };
        return jdbcTemplate.query(query, sectionRowMapper, lineId);
    }
}
