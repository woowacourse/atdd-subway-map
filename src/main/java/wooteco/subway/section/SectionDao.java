package wooteco.subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.Station;

import java.util.List;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Section> sectionRowMapper() {
        return (resultSet, rowNum) -> {
            Station upStation = new Station(
                    resultSet.getLong("up_station_id"),
                    resultSet.getString("up_station_name")
            );
            Station downStation = new Station(
                    resultSet.getLong("down_station_id"),
                    resultSet.getString("down_station_name")
            );

            return new Section(upStation, downStation, resultSet.getInt("distance"));
        };
    }

    public void save(Long lineId, Long upStationId, Long downStationId, int distance) {
        String sql = "insert into SECTION (LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId, upStationId, downStationId, distance);
    }

    public void deleteSectionsOf(Long lineId) {
        String sql = "delete from SECTION where LINE_ID = ?";
        jdbcTemplate.update(sql, lineId);
    }

    public List<Section> findSections(Long lineId) {
        String sql = "select SEC.UP_STATION_ID as UP_STATION_ID, S1.name as UP_STATION_NAME, " +
                "SEC.DOWN_STATION_ID as DOWN_STATION_ID, S2.name as DOWN_STATION_NAME, " +
                "DISTANCE " +
                "from SECTION SEC " +
                "left outer join STATION S1 on SEC.up_station_id = S1.id " +
                "left outer join STATION S2 on SEC.down_station_id = S2.id " +
                "where SEC.LINE_ID = ?";
        return jdbcTemplate.query(sql, sectionRowMapper(), lineId);
    }
}
