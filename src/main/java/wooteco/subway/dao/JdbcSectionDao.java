package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> sectionRowMapper = ((rs, rowNum) -> new Section(
            rs.getLong("id"),
            rs.getLong("line_id"),
            new Station(rs.getLong("us_id"), rs.getString("us_name")),
            new Station(rs.getLong("ds_id"), rs.getString("ds_name")),
            rs.getInt("distance")
    ));

    public JdbcSectionDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(final Section section) {
        final String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values(?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStation().getId());
            ps.setLong(3, section.getDownStation().getId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Section(id, section);
    }

    @Override
    public List<Section> findAllByLineId(final long lineId) {
        final String sql = "select s.id as id, s.line_id as line_id, us.id as us_id, us.name as us_name,"
                + "ds.id as ds_id, ds.name as ds_name, s.distance as distance from SECTION as s "
                + "join STATION as us on us.id = s.up_station_id "
                + "join STATION as ds on ds.id = s.down_station_id "
                + "where line_id = ?";

        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }
}
