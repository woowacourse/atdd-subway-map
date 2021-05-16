package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> new Section(
        resultSet.getLong("LINE_ID"),
        resultSet.getLong("UP_STATION_ID"),
        resultSet.getLong("DOWN_STATION_ID"),
        resultSet.getInt("DISTANCE")
    );

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Section section) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[] {"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public Sections findSectionsByLineId(long id) {
        String sql = "SELECT * FROM SECTION WHERE LINE_ID = ?";
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, id);

        List<Section> sections1 = new ArrayList<>();

        for (Map<String, Object> result : resultList) {
            long up_station_id = (long)result.get("UP_STATION_ID");
            long down_station_id = (long)result.get("DOWN_STATION_ID");
            int distance = (int)result.get("DISTANCE");

            sections1.add(new Section(id, up_station_id, down_station_id, distance));
        }
        return Sections.of(sections1);
    }

    public Optional<Section> findSectionBySameUpStation(long lineId, long upStationId) {
        String sql = "SELECT * FROM SECTION WHERE LINE_ID = ? AND UP_STATION_ID = ?";
        List<Section> result = jdbcTemplate.query(sql, sectionRowMapper, lineId, upStationId);
        return result.stream().findAny();
    }

    public Optional<Section> findSectionBySameDownStation(long lineId, long downStationId) {
        String sql = "SELECT * FROM SECTION WHERE LINE_ID = ? AND DOWN_STATION_ID = ?";
        List<Section> result = jdbcTemplate.query(sql, sectionRowMapper, lineId, downStationId);
        return result.stream().findAny();
    }

    public int updateDownStation(Section originSection, long newStationId) {
        String sql = "UPDATE SECTION set DOWN_STATION_ID = ?, DISTANCE = ? WHERE LINE_ID = ? AND DOWN_STATION_ID = ?";
        return jdbcTemplate.update(sql, newStationId, originSection.getDistance(), originSection.getLineId(),
            originSection.getDownStationId());
    }

    public int updateUpStation(Section originSection, long newStationId) {
        String sql = "UPDATE SECTION set UP_STATION_ID = ?, DISTANCE = ? WHERE LINE_ID = ? AND UP_STATION_ID = ?";
        return jdbcTemplate.update(sql, newStationId, originSection.getDistance(), originSection.getLineId(),
            originSection.getUpStationId());
    }

    public int deleteSection(Section section) {
        String sql = "DELETE FROM SECTION where UP_STATION_ID = ?";
        return jdbcTemplate.update(sql, section.getUpStationId());
    }
}
