package wooteco.subway.infrastructure.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.section.Section;
import wooteco.subway.domain.line.section.rule.SectionDeleteRule.SectionDeleteRuleFactory;
import wooteco.subway.domain.line.value.line.LineId;
import wooteco.subway.domain.line.value.section.Distance;
import wooteco.subway.domain.line.value.section.SectionId;
import wooteco.subway.domain.station.value.StationId;

import java.sql.PreparedStatement;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Section> save(List<Section> sections) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ? ,?)";

        List<Object[]> argsOfSections = sections.stream().map(
                section -> {
                    Long lineId = section.getLineId();
                    Long upStationId = section.getUpStationId();
                    Long downStationId = section.getDownStationId();
                    Long distance = section.getDistance();

                    return new Object[]{lineId, upStationId, downStationId, distance};
                }
        ).collect(toList());

        jdbcTemplate.batchUpdate(sql, argsOfSections);

        return sections;
    }

    public Section save(Section section) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
                    PreparedStatement pstmt = connection.prepareStatement(sql, new String[]{"id"});
                    pstmt.setLong(1, section.getLineId());
                    pstmt.setLong(2,section.getUpStationId());
                    pstmt.setLong(3, section.getDownStationId());
                    pstmt.setLong(4, section.getDistance());

                    return pstmt;
                },
                keyHolder
        );

        return new Section(
                new SectionId(keyHolder.getKeyAs(Long.class)),
                new LineId(section.getLineId()),
                new StationId(section.getUpStationId()),
                new StationId(section.getDownStationId()),
                new Distance(section.getDistance())
        );
    }

    public Section findById(Long id) {
        String sql = "SELECT * FROM SECTION WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, createSection(), id);
    }

    public List<Section> findAll() {
        String sql = "SELECT * FROM SECTION";

        return jdbcTemplate.query(sql, createSection());
    }

    private RowMapper<Section> createSection() {
        return (rs, rowNum) -> {
            Long lineId = rs.getLong("line_id");
            Long upStationId = rs.getLong("up_station_id");
            Long downStationId = rs.getLong("down_station_id");
            Long distance = rs.getLong("distance");

            return new Section(
                    new LineId(lineId),
                    new StationId(upStationId),
                    new StationId(downStationId),
                    new Distance(distance)
            );
        };
    }

    public List<Section> findAllByLineId(Long lineId) {
        String sql = "SELECT * FROM SECTION WHERE line_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            Long upStationId = rs.getLong("up_station_id");
            Long downStationId = rs.getLong("down_station_id");
            Long distance = rs.getLong("distance");

            return new Section(
                    new SectionId(id),
                    new LineId(lineId),
                    new StationId(upStationId),
                    new StationId(downStationId),
                    new Distance(distance)
            );
        }, lineId);
    }

    public void update(List<Section> sections) {
        final String sql = "UPDATE SECTION SET " +
                "up_station_id = ?, " +
                "down_station_id = ?, " +
                "distance = ? " +
                "WHERE id = ?";

        List<Object[]> argsOfSections = sections.stream().map(
                section -> {
                    Long upStationId = section.getUpStationId();
                    Long downStationId = section.getDownStationId();
                    Long distance = section.getDistance();
                    Long id = section.getId();

                    return new Object[]{upStationId, downStationId, distance, id};
                }
        ).collect(toList());

        jdbcTemplate.batchUpdate(sql, argsOfSections);
    }

    public void delete(List<Section> sections) {
        final String sql = "DELETE FROM SECTION WHERE id = ?";

        List<Object[]> argsOfSections = sections.stream().map(
                section -> {
                    Long id = section.getId();

                    return new Object[]{id};
                }
        ).collect(toList());

        jdbcTemplate.batchUpdate(sql, argsOfSections);
    }
}
