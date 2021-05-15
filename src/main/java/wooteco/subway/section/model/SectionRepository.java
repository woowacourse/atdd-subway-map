package wooteco.subway.section.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.model.Line;
import wooteco.subway.station.model.Station;

@Repository
public class SectionRepository {

    private JdbcTemplate jdbcTemplate;

    public SectionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Section> findSectionsByLineId(Long id) {
        String sql = "SELECT L.id line_id, L.name line_name, L.color line_color,"
            + "US.id us_id, US.name us_name, DS.id ds_id, DS.name ds_name, "
            + "S.id s_id, S.distance s_distance"
            + " FROM section AS S"
            + " JOIN line AS L"
            + " ON S.line_id = L.id"
            + " JOIN station AS US"
            + " ON S.up_station_id = US.id"
            + " JOIN station AS DS"
            + " ON S.down_station_id = DS.id"
            + " WHERE L.id = ?";
        return jdbcTemplate.query(sql, mapperToSection(), id);
    }

    private RowMapper<Section> mapperToSection() {
        return (rs, rowNum) -> Section.builder()
            .id(rs.getLong("s_id"))
            .line(mapperToLine(rs))
            .upStation(mapperToUpStation(rs))
            .downStation(mapperToDownStation(rs))
            .distance(rs.getInt("s_distance"))
            .build();
    }

    private Line mapperToLine(java.sql.ResultSet rs) throws SQLException {
        Long lineId = rs.getLong("line_id");
        String lineName = rs.getString("line_name");
        String lineColor = rs.getString("line_color");
        Line line = new Line(lineId, lineName, lineColor);
        return line;
    }

    private Station mapperToUpStation(ResultSet rs) throws SQLException {
        Long upStationId = rs.getLong("us_id");
        String upStationName = rs.getString("us_name");
        return new Station(upStationId, upStationName);
    }

    private Station mapperToDownStation(ResultSet rs) throws SQLException {
        Long downStationId = rs.getLong("ds_id");
        String downStationName = rs.getString("ds_name");
        Station downStation = new Station(downStationId, downStationName);
        return downStation;
    }
}
