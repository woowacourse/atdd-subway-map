package wooteco.subway.repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Long lineId, Section section) {
        String query = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = (connection) -> {
            PreparedStatement prepareStatement = connection
                .prepareStatement(query, new String[]{"id"});
            prepareStatement.setLong(1, lineId);
            prepareStatement.setLong(2, section.getUpStationId());
            prepareStatement.setLong(3, section.getDownStationId());
            prepareStatement.setInt(4, section.getDistance());
            return prepareStatement;
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public Sections findByLineId(Long lineId) {
        String query = "SELECT id, up_station_id, down_station_id, distance, "
            + "(SELECT name FROM STATION WHERE STATION.id = SECTION.up_station_id) AS upStation, "
            + "(SELECT name FROM STATION WHERE STATION.id = SECTION.down_station_id) AS downStation, "
            + "FROM SECTION WHERE line_id = ?";
        List<Section> sections = jdbcTemplate.query(query, (rs, rowNum) -> {
            long sectionId = rs.getLong("id");
            Station upStation = new Station(rs.getLong("up_station_id"), rs.getString("upStation"));
            Station downStation = new Station(rs.getLong("down_station_id"),
                rs.getString("downStation"));
            int distance = rs.getInt("distance");
            return new Section(sectionId, upStation, downStation, distance);
        }, lineId);
        return new Sections(sections);
    }
}
