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
import wooteco.subway.domain.Sections;

@Repository
public class JdbcSectionDao implements SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private final RowMapper<Section> rowMapper = (rs, rowNum) ->
            new Section(
                    rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getLong("up_station_id"),
                    rs.getLong("down_station_id"),
                    rs.getInt("distance")
            );

    public JdbcSectionDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Section save(Section section) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(section);
        long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return new Section(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(),
                section.getDistance());
    }

    @Override
    public Section findById(Long id) {
        String sql = "SELECT * FROM SECTION WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    @Override
    public Sections findByLineId(Long lineId) {
        String sql = "SELECT * FROM SECTION WHERE line_id = ?";
        return new Sections(jdbcTemplate.query(sql, rowMapper, lineId));
    }

    @Override
    public int updateSection(Section section) {
        String sql = "UPDATE SECTION SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        return jdbcTemplate.update(sql, section.getId(), section.getUpStationId(), section.getDownStationId(),
                section.getDistance(), section.getId());
    }

    @Override
    public List<Section> findAll() {
        String sql = "SELECT * FROM SECTION";
        return jdbcTemplate.query(sql, rowMapper);
    }
}
