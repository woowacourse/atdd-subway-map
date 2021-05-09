package wooteco.subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.domain.Distance;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.Arrays;

@Repository
public class H2SectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private static final RowMapper<Section> SECTION_ROW_MAPPER = (rs, rowNum) -> {
        long id = rs.getLong("id");
        long lineId = rs.getLong("line_id");
        long upStationId = rs.getLong("up_station_id");
        long downStationId = rs.getLong("down_station_id");
        int distance = rs.getInt("distance");
        String upStationName = rs.getString(7);
        String downStationName = rs.getString(9);
        return new Section(id, lineId,
                Arrays.asList(new Station(upStationId, upStationName), new Station(downStationId, downStationName)),
                new Distance(distance));
    };

    public H2SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(Section section) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getStations().get(0).getId());
            ps.setLong(3, section.getStations().get(1).getId());
            ps.setInt(4, section.getDistance().distance());
            return ps;
        }, keyHolder);
        long sectionId = keyHolder.getKey().longValue();
        return new Section(sectionId, section.getLineId(), section.getStations(), section.getDistance());
    }

    @Override
    public Sections findByLineId(Long lineId) {
        String sql = "SELECT * " +
                "FROM SECTION AS s " +
                "JOIN STATION AS t ON s.up_station_id = t.id " +
                "JOIN STATION AS a ON s.down_station_id = a.id " +
                "WHERE line_id = ?";
        return new Sections(jdbcTemplate.query(sql, SECTION_ROW_MAPPER, lineId));
    }
}