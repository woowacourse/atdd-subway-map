package wooteco.subway.dao;

import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.entity.SectionEntity2;
import wooteco.subway.entity.StationEntity;

@Repository
public class SectionDao2 {

    private static final RowMapper<SectionEntity2> ROW_MAPPER = (resultSet, rowNum) -> {
        Long lineId = resultSet.getLong("line_id");
        StationEntity upStation = new StationEntity(
                resultSet.getLong("up_station_id"),
                resultSet.getString("up_station_name"));
        StationEntity downStation = new StationEntity(
                resultSet.getLong("down_station_id"),
                resultSet.getString("down_station_name"));
        int distance = resultSet.getInt("distance");
        return new SectionEntity2(lineId, upStation, downStation, distance);
    };

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SectionDao2(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SectionEntity2> findAllByLineId(Long lineId) {
        final String sql = "SELECT A.line_id AS line_id, A.distance AS distance, "
                + "B.id AS up_station_id, B.name AS up_station_name, "
                + "C.id AS down_station_id, C.name AS down_station_name "
                + "FROM section A, station B, station C "
                + "WHERE A.up_station_id = B.id AND A.down_station_id = C.id "
                + "AND A.line_id = :lineId";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("lineId", lineId);

        return jdbcTemplate.query(sql, paramSource, ROW_MAPPER);
    }
}
