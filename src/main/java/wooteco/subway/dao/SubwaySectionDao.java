package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
public class SubwaySectionDao implements SectionDao<Section> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> sectionRowMapper = (result, rowNum) -> new Section(
            result.getLong("sid"),
            new Line(),
            new Station(result.getLong("usid"), result.getString("usname")),
            new Station(result.getLong("dsid"), result.getString("dsname")),
            result.getInt("sdis")
    );

    public SubwaySectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(Section section) {
        final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("section").usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("line_id", section.getLine().getId());
        parameters.put("up_station_id", section.getUpStation().getId());
        parameters.put("down_station_id", section.getDownStation().getId());
        parameters.put("distance", section.getDistance());

        final Number number = simpleJdbcInsert.executeAndReturnKey(parameters);
        return new Section(number.longValue(), section.getLine(), section.getUpStation(), section.getDownStation(),
                section.getDistance());
    }

    @Override
    public int deleteSectionById(List<Long> ids) {
        String sql = "DELETE FROM section WHERE id=?";
        List<Object[]> batch = changeToObjects(ids);
        return jdbcTemplate.batchUpdate(sql, batch).length;
    }

    private List<Object[]> changeToObjects(List<Long> ids) {
        return ids.stream()
                .map(id -> new Object[]{id})
                .collect(Collectors.toList());
    }

    @Override
    public List<Section> findByLineId(Long lineId) {
        String sql =
                "SELECT s.id sid, us.id usid, us.name usname, ds.id dsid, ds.name dsname, s.distance sdis FROM section s "
                        + "JOIN station us ON us.id=s.up_station_id "
                        + "JOIN station ds ON ds.id=s.down_station_id "
                        + "WHERE s.line_id=?";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }

    @Override
    public int updateUpStationSection(Long lineId, Long originUpStationId, Long upStationId, int distance) {
        String sql = "UPDATE section SET up_station_id=?, distance=? WHERE line_id=? AND up_station_id=?";
        return jdbcTemplate.update(sql, upStationId, distance, lineId, originUpStationId);
    }

    @Override
    public int updateDownStationSection(Long lineId, Long originDownStationId, Long downStationId, int distance) {
        String sql = "UPDATE section SET down_station_id=?, distance=? WHERE line_id=? AND down_station_id=?";
        return jdbcTemplate.update(sql, downStationId, distance, lineId, originDownStationId);
    }

    @Override
    public int countByLineId(Long lineId) {
        String sql = "SELECT count(*) FROM section where line_id=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, lineId);
    }

    @Override
    public List<Section> findByLineIdAndStationId(Long lineId, Long stationId) {
        String sql =
                "SELECT s.id sid, us.id usid, us.name usname, ds.id dsid, ds.name dsname, s.distance sdis FROM section s "
                        + "JOIN station us ON us.id=s.up_station_id "
                        + "JOIN station ds ON ds.id=s.down_station_id "
                        + "WHERE s.line_id=? AND (s.up_station_id=? OR s.down_station_id=?)";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId, stationId, stationId);
    }
}
