package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class SectionDao {
    private JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long insert(long lineId, Section section) {
        String query = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeSaveQuery(lineId, section, query, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private void executeSaveQuery(long lineId, Section section, String query, KeyHolder keyHolder) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);
    }

    public List<Section> selectAll(long id) {
        String query = "SELECT * FROM section WHERE line_id = ?";
        List<Section> sections = jdbcTemplate.query(query, (resultSet, rowNum) -> {
            Section section = new Section(
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance")
            );
            return section;
        }, id);
        return sections;
    }
}
