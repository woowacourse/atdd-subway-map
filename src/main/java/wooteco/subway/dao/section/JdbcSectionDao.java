package wooteco.subway.dao.section;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Section save(Section section, Long lineId) {
        String sql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setLong(1, lineId);
            preparedStatement.setLong(2, section.getUpStation().getId());
            preparedStatement.setLong(3, section.getDownStation().getId());
            preparedStatement.setInt(4, section.getDistance());
            return preparedStatement;
        }, keyHolder);
        return Section.of(keyHolder.getKey().longValue(), lineId, section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    @Override
    public Sections findSectionsByLineId(Long lineId) {
        String sql = "SELECT id, (SELECT * FROM station WHERE station.id = section.up_station_id) AS upStation, " +
                "(SELECT * FROM station WHERE station.id = section.down_station_id) AS downStation, " +
                "distance FROM section WHERE line_id = ?;";
        List<Section> sections = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long foundId = rs.getLong("id");
            Station upStation = convertRowToStation(rs.getString("upStation"));
            Station downStation = convertRowToStation(rs.getString("downStation"));
            int distance = rs.getInt("distance");

            return Section.of(foundId, lineId, upStation, downStation, distance);
        }, lineId);

        return Sections.from(sections);
    }

    private Station convertRowToStation(String row) {
        String substring = row.substring(5, row.length() - 1).trim();
        String[] split = substring.split(", ");
        long id = Long.parseLong(String.valueOf(split[0]));
        return Station.of(id, split[1]);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM section WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Section> findSectionContainsStationId(Long lineId, Long stationId) {
        Sections sectionsByLineId = findSectionsByLineId(lineId);
        return sectionsByLineId.containsStationByStationId(stationId);
    }

    @Override
    public void deleteStations(Long lineId, List<Section> sections) {
        String sql = "DELETE FROM section WHERE id = ?";
        for (Section section : sections) {
            jdbcTemplate.update(sql, section.getId());
        }
    }

    @Override
    public void insertSection(Section affectedSection, Long lineId) {
        save(affectedSection, lineId);
    }

    private RowMapper getRowMapper() {
        RowMapper rowMapper = new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = rs.getLong("id");
                Long lineId = rs.getLong("line_id");
                Long upStationId = rs.getLong("up_station_id");
                Long downStationId = rs.getLong("down_station_id");
                Integer distance = rs.getInt("distance");
                return Section.of(id, lineId, Station.from("111"), Station.from("222"), distance);
            }
        };
        return rowMapper;
    }
}
