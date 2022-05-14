package wooteco.subway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;
    private final RowMapper<Section> sectionRowMapper = (rs, rowNum) -> new Section(
        rs.getLong("id"),
        rs.getLong("line_id"),
        rs.getLong("up_station_id"),
        rs.getLong("down_station_id"),
        rs.getInt("distance"),
        rs.getLong("line_order")
    );

    public JdbcSectionDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        insertActor = new SimpleJdbcInsert(dataSource)
            .withTableName("SECTION")
            .usingGeneratedKeyColumns("id");
    }

    @Override
    public Long save(Section section) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(section);
        return insertActor.executeAndReturnKey(parameters).longValue();
    }

    @Override
    public void updateLineOrderByInc(long lineId, Long lineOrder) {
        String sql = "UPDATE \"SECTION\" SET line_order = line_order + 1 WHERE line_id = (?) AND line_order >= (?)";
        jdbcTemplate.update(sql, lineId, lineOrder);
    }

    @Override
    public boolean existByLineId(long lineId) {
        String sql = "SELECT EXISTS ("
            + "SELECT * FROM \"SECTION\" WHERE line_id = (?)"
            + ")";
        return jdbcTemplate.queryForObject(sql, boolean.class, lineId);
    }

    @Override
    public List<Section> findAllByLineId(long lineId) {
        String sql = "SELECT * from \"SECTION\" WHERE line_id = (?)";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM \"SECTION\" WHERE id = (?)";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Section> findByLineIdAndStationId(long lineId, long stationId) {
        String sql = "SELECT * FROM \"SECTION\""
            + " WHERE line_id = (?) AND (up_station_id = (?) OR down_station_id = (?))";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId, stationId, stationId);
    }

    @Override
    public void updateLineOrderByDec(long lineId, Long lineOrder) {
        String sql = "UPDATE \"SECTION\" SET line_order = line_order - 1 WHERE line_id = (?) AND line_order > (?)";
        jdbcTemplate.update(sql, lineId, lineOrder);
    }
}
