package wooteco.subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class JDBCSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public JDBCSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> actorRowMapper = (resultSet, rowNum) ->
            new Section(
                    resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance")
            );

    @Override
    public Section save(Section section) {
        String query = "INSERT INTO SECTION(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, String.valueOf(section.getLineId()));
            ps.setString(2, String.valueOf(section.getUpStationId()));
            ps.setString(3, String.valueOf(section.getDownStationId()));
            ps.setString(4, String.valueOf(section.getDistance()));
            return ps;
        }, keyHolder);

        return new Section(
                keyHolder.getKey().longValue(),
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance()
        );
    }

    @Override
    public Sections findByLineId(Long lineId) {
        String query = "SELECT * FROM SECTION WHERE line_id = ?";

        List<Section> sections = this.jdbcTemplate.query(query, actorRowMapper, lineId);

        return new Sections(sections);
    }

    @Override
    public void update(Section updateSection) {
        String query = "UPDATE SECTION SET line_id = ?, up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        this.jdbcTemplate.update(query, updateSection.getLineId(),
                updateSection.getUpStationId(), updateSection.getDownStationId(),
                updateSection.getDistance(), updateSection.getId());
    }

    @Override
    public void delete(Long lineId, Long stationId) {
        String query = "DELETE FROM SECTION WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        this.jdbcTemplate.update(query, lineId, stationId, stationId);
    }
}
