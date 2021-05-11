package wooteco.subway.section.dao;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.domain.OrderedSections;
import wooteco.subway.section.domain.Section;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<SectionDto> SECTION_DTO_ROW_MAPPER = (rs, rowNum) -> {
        long id = rs.getLong("id");
        long upStationId = rs.getLong("up_station_id");
        long downStationId = rs.getLong("down_station_id");
        long distance = rs.getLong("distance");
        return new SectionDto(id, upStationId, downStationId, distance);
    };

    public Section save(Long lineId, Section section) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, section.getUpStation().getId());
            ps.setLong(3, section.getDownStation().getId());
            ps.setLong(4, section.getDistance());
            return ps;
        }, keyHolder);

        long sectionId = keyHolder.getKey().longValue();
        return new Section(sectionId, section);
    }

    public List<SectionDto> findByLineId(Long id) {
        String sql = "SELECT * " +
                "FROM SECTION " +
                "WHERE line_id = ?";

        return jdbcTemplate.query(sql,
                SECTION_DTO_ROW_MAPPER,
                id
        );
    }

    public void save(Long lineId, OrderedSections lineSections) {
        deleteLine(lineId);

        String sql = "INSERT INTO " +
                "SECTION (line_id, up_station_id, down_station_id, distance) VALUES " +
                "(?, ?, ?, ?)";

        List<Section> sections = lineSections.getSections();
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Section section = sections.get(i);
                ps.setLong(1, lineId);
                ps.setLong(2, section.getUpStation().getId());
                ps.setLong(3, section.getDownStation().getId());
                ps.setLong(4, section.getDistance());
            }

            @Override
            public int getBatchSize() {
                return sections.size();
            }
        });
    }

    public void deleteLine(Long lineId) {
        String sql = "DELETE FROM SECTION " +
                "WHERE line_id = ?";

        jdbcTemplate.update(sql, lineId);
    }

    public long count(Long lineId) {
        String sql = "SELECT COUNT(*) as CNT " +
                "FROM SECTION " +
                "WHERE line_id = ?";

        return jdbcTemplate.queryForObject(sql, Long.class, lineId);
    }
}
