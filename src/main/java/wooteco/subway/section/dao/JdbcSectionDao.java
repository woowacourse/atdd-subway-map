package wooteco.subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(Section section) {
        String query = "INSERT INTO SECTION(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, String.valueOf(section.getLineId()));
            ps.setString(2, String.valueOf(section.getUpStation().getId()));
            ps.setString(3, String.valueOf(section.getDownStation().getId()));
            ps.setString(4, String.valueOf(section.getDistance()));
            return ps;
        }, keyHolder);

        return new Section(
                keyHolder.getKey().longValue(),
                section.getLineId(),
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance()
        );
    }

    @Override
    public Sections findByLineId(Long lineId) {
        String query =
                "SELECT s.id AS section_id, line_id, " +
                        "up_table.id AS up_id, " +
                        "up_table.name AS up_name, " +
                        "down_table.id AS down_id, " +
                        "down_table.name AS down_name, " +
                        "distance FROM section AS s\n" +
                        "LEFT JOIN station AS up_table ON s.up_station_id = up_table.id\n" +
                        "LEFT JOIN station AS down_table ON s.down_station_id = down_table.id\n" +
                        "WHERE s.line_id = ?";

        List<Section> sections = this.jdbcTemplate.query(query, (resultSet, rowNum) -> new Section(
                resultSet.getLong("section_id"),
                lineId,
                new Station(resultSet.getLong("up_id"), resultSet.getString("up_name")),
                new Station(resultSet.getLong("down_id"), resultSet.getString("down_name")),
                resultSet.getInt("distance")
        ), lineId);

        return new Sections(sections);
    }

    @Override
    public void update(Section updateSection) {
        String query = "UPDATE SECTION SET line_id = ?, up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        this.jdbcTemplate.update(query, updateSection.getLineId(),
                updateSection.getUpStation().getId(), updateSection.getDownStation().getId(),
                updateSection.getDistance(), updateSection.getId());
    }

    @Override
    public void delete(Long lineId, Station station) {
        String query = "DELETE FROM SECTION WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        this.jdbcTemplate.update(query, lineId, station.getId(), station.getId());
    }
}
