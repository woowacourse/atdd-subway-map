package wooteco.subway.dao;

import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<Section> eventRowMapper = (resultSet, rowNum)
            -> new Section(resultSet.getLong("id")
            , resultSet.getLong("line_id")
            , resultSet.getLong("up_station_id")
            , resultSet.getLong("down_station_id")
            , resultSet.getInt("distance"));

    public SectionDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Long save(Section section) {
        String insertSql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (:lineId, :upStationId, :downStationId, :distance)";
        SqlParameterSource source = new BeanPropertySqlParameterSource(section);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(insertSql, source, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public List<Section> findByLineId(Long lineId) {
        String selectSql = "select * from SECTION where line_id = :lineId";
        SqlParameterSource source = new MapSqlParameterSource("lineId", lineId);
        return jdbcTemplate.query(selectSql, source, eventRowMapper);
    }
}
