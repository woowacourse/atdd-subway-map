package wooteco.subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.common.exception.not_found.NotFoundSectionInfoException;
import wooteco.subway.section.domain.Distance;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class H2SectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Section> SECTION_ROW_MAPPER = (rs, rowNum) -> {
        long id = rs.getLong("id");
        long lineId = rs.getLong("line_id");
        long upStationId = rs.getLong("up_station_id");
        long downStationId = rs.getLong("down_station_id");
        int distance = rs.getInt("distance");
        String upStationName = rs.getString(7);
        String downStationName = rs.getString(9);
        return new Section(id, lineId,
                new Station(upStationId, upStationName), new Station(downStationId, downStationName),
                new Distance(distance));
    };

    public H2SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(Section section) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStation().getId());
            ps.setLong(3, section.getDownStation().getId());
            ps.setInt(4, section.getDistance().distance());
            return ps;
        }, keyHolder);
        long sectionId = keyHolder.getKey().longValue();
        return new Section(sectionId, section.getLineId(), section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    @Override
    public Section findById(Long id) {
        String sql = "SELECT * " +
                "FROM SECTION AS s " +
                "JOIN STATION AS t ON s.up_station_id = t.id " +
                "JOIN STATION AS a ON s.down_station_id = a.id " +
                "WHERE s.id = ?";

        List<Section> sections = jdbcTemplate.query(sql,
                SECTION_ROW_MAPPER, id);

        if (sections.isEmpty()) {
            throw new NotFoundSectionInfoException(String.format("데이터베이스에 해당 ID의 구간이 없습니다. ID : %d", id));
        }

        return sections.get(0);
    }

    @Override
    public Sections findByLineId(Long lineId) {
        String sql = "SELECT * " +
                "FROM SECTION AS s " +
                "JOIN STATION AS t ON s.up_station_id = t.id " +
                "JOIN STATION AS a ON s.down_station_id = a.id " +
                "WHERE line_id = ?";
        return new Sections(jdbcTemplate.query(sql, SECTION_ROW_MAPPER, lineId));
    }

    @Override
    public void update(Section section) {
        String sql = "UPDATE SECTION " +
                "SET up_station_id = ?, down_station_id = ?, distance = ? " +
                "WHERE id = ?";

        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        jdbcTemplate.update(sql, upStation.getId(), downStation.getId(),
                section.getDistance().distance(), section.getId());
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE " +
                "FROM SECTION";

        jdbcTemplate.update(sql);
    }

    @Override
    public void delete(Section section) {
        String sql = "DELETE " +
                "FROM SECTION " +
                "WHERE id = ?";

        jdbcTemplate.update(sql, section.getId());
    }

    @Override
    public List<Section> findByLineIdAndStationId(Long lineId, Long stationId) {
        String sql = "SELECT * " +
                "FROM SECTION AS s " +
                "JOIN STATION AS t ON s.up_station_id = t.id " +
                "JOIN STATION AS a ON s.down_station_id = a.id " +
                "WHERE line_id = ? AND s.up_station_id = ? OR s.down_station_id = ?";
        return jdbcTemplate.query(sql, SECTION_ROW_MAPPER, lineId, stationId, stationId);
    }

    @Override
    public boolean canDelete(Long lineId) {
        String sql = "SELECT COUNT(*) " +
                "FROM SECTION " +
                "WHERE line_id = ?";

        int countOfSection = jdbcTemplate.queryForObject(sql, Integer.class, lineId);
        return countOfSection < 2;
    }
}