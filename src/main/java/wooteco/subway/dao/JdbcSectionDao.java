package wooteco.subway.dao;

import java.sql.PreparedStatement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import wooteco.subway.domain.Section;

public class JdbcSectionDao implements SectionDao{

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Section> stationRowMapper = (resultSet, rowNum) -> new Section(
            resultSet.getLong("upStationId"),
            resultSet.getLong("downStationId"),
            resultSet.getInt("distance")
    );

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Section section) {
        final String sql = "INSERT INTO SECTION (up_station_id, down_station_id, distance) VALUES (?, ?, ?)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, Long.toString(section.getUpStationId()));
            ps.setString(2, Long.toString(section.getDownStationId()));
            ps.setString(3, Long.toString(section.getDistance()));
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public void deleteById(Long id) {
        final String sql = "DELETE FROM SECTION WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
