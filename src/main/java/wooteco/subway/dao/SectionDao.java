package wooteco.subway.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.LineRequest;

import javax.sql.DataSource;

@Repository
public class SectionDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
            new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color"));
    ;

    public SectionDao(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Section save(LineRequest lineRequest, Long id){
        Long upStationId = lineRequest.getUpStationId();
        Long downStationId = lineRequest.getDownStationId();
        int distance = lineRequest.getDistance();

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("line_id", id)
                .addValue("upStationId", upStationId)
                .addValue("downStationId", downStationId)
                .addValue("distance", distance);

        Long sectionId = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Section(id, upStationId, downStationId, distance);
    }
}
