package wooteco.subway.dao;

import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.entity.SectionViewEntity;
import wooteco.subway.entity.StationEntity;

@Repository
public class SectionViewDao {

    private static final RowMapper<SectionViewEntity> ROW_MAPPER = (resultSet, rowNum) -> {
        StationEntity upStation = new StationEntity(
                resultSet.getLong("up_station_id"),
                resultSet.getString("up_station_name"));
        StationEntity downStation = new StationEntity(
                resultSet.getLong("down_station_id"),
                resultSet.getString("down_station_name"));
        return new SectionViewEntity(resultSet.getLong("line_id"),
                upStation, downStation, resultSet.getInt("distance"));
    };

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SectionViewDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SectionViewEntity> findAllByLineId(Long lineId) {
        final String sql = "SELECT A.line_id, A.distance, B.id AS up_station_id, "
                + "B.name AS up_station_name, C.id AS down_station_id, C.name AS down_station_name "
                + "FROM section A, station B, station C "
                + "WHERE A.up_station_id = B.id AND A.down_station_id = C.id "
                + "AND line_id = :lineId";

        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("lineId", lineId);

        return jdbcTemplate.query(sql, paramSource, ROW_MAPPER);
    }
}
