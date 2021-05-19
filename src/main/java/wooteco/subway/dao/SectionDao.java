package wooteco.subway.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private final RowMapper<Section> sectionRowMapper = (rs, rowNum) -> {
        final long id = rs.getLong("id");
        final long upStationId = rs.getLong("up_station_id");
        final String upStationName = rs.getString("ups.name");
        final Station upStation = new Station(upStationId, upStationName);

        final long downStationId = rs.getLong("down_station_id");
        final String downStationName = rs.getString("dos.name");
        final Station downStation = new Station(downStationId, downStationName);

        final int distance = rs.getInt("distance");
        return new Section(id, upStation, downStation, distance);
    };

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(jdbcTemplate).withTableName("SECTION").usingGeneratedKeyColumns("id");
    }

    public Section save(final Long lineId, final Section section) {
        MapSqlParameterSource params = parametersToInsert(lineId, section);
        Long generatedId = insertAction.executeAndReturnKey(params).longValue();
        return new Section(generatedId, section);
    }

    private MapSqlParameterSource parametersToInsert(Long lineId, Section section) {
        return new MapSqlParameterSource().addValue("line_id", lineId)
                                          .addValue("up_station_id", section.getUpStation().getId())
                                          .addValue("down_station_id", section.getDownStation().getId())
                                          .addValue("distance", section.getDistance());
    }

    public void saveAll(Long lineId, Sections sections) {
        final MapSqlParameterSource[] paramsGroup = sections.getSections()
                                                            .stream()
                                                            .map(section -> parametersToInsert(lineId, section))
                                                            .toArray(MapSqlParameterSource[]::new);
        insertAction.executeBatch(paramsGroup);
    }

    public List<Section> findAllByLineId(final Long lineId) {
        String sql = "SELECT s.id, s.up_station_id, s.down_station_id, s.distance, ups.name, dos.name " +
                "FROM SECTION s " +
                "JOIN STATION ups ON s.up_station_id = ups.id " +
                "JOIN STATION dos ON s.down_station_id = dos.id " +
                "WHERE s.line_id = ?";

        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }

    public void updateByLineIdAndUpStationId(final Long lineId, final Section section) {
        String sql = "UPDATE SECTION s " +
                "SET s.down_station_id = ?, s.distance = ? " +
                "WHERE s.line_id = ? AND s.up_station_id = ?";

        final Long upStationId = section.getUpStation().getId();
        final Long downStationId = section.getDownStation().getId();
        jdbcTemplate.update(sql, upStationId, section.getDistance(), lineId, downStationId);
    }

    public void updateByLineIdAndDownStationId(final Long lineId, final Section section) {
        String sql = "UPDATE SECTION s " +
                "SET s.up_station_id = ?, s.distance = ? " +
                "WHERE s.line_id = ? AND s.down_station_id = ?";

        final Long upStationId = section.getUpStation().getId();
        final Long downStationId = section.getDownStation().getId();
        jdbcTemplate.update(sql, upStationId, section.getDistance(), lineId, downStationId);
    }

    public void deleteByLineIdAndUpStationId(final Long lineId, final Long upStationId) {
        String sql = "DELETE FROM SECTION s WHERE s.line_id = ? AND s.up_station_id = ?";
        jdbcTemplate.update(sql, lineId, upStationId);
    }

    public void deleteByLineIdAndDownStationId(final Long lineId, final Long downStationId) {
        String sql = "DELETE FROM SECTION s WHERE s.line_id = ? AND s.down_station_id = ?";
        jdbcTemplate.update(sql, lineId, downStationId);
    }

    public void deleteAllByLineId(Long lineId) {
        String sql = "DELETE FROM SECTION s WHERE s.line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }
}
