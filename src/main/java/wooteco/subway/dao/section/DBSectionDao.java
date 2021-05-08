package wooteco.subway.dao.section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.entity.SectionEntity;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class DBSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<SectionEntity> sectionEntityRowMapper;

    @Autowired
    public DBSectionDao(final JdbcTemplate jdbcTemplate) {
        this(jdbcTemplate,
                (rs, rowNum) ->
                        new SectionEntity(rs.getLong("id"),
                                rs.getLong("line_id"),
                                rs.getLong("up_station_id"),
                                rs.getLong("down_station_id"),
                                rs.getInt("distance")));
    }

    public DBSectionDao(final JdbcTemplate jdbcTemplate, final RowMapper<SectionEntity> sectionEntityRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.sectionEntityRowMapper = sectionEntityRowMapper;
    }

    @Override
    public SectionEntity save(final SectionEntity sectionEntity) {
        String sql = "INSERT INTO SECTION(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, sectionEntity.getLineId());
            ps.setLong(2, sectionEntity.getUpStationId());
            ps.setLong(3, sectionEntity.getDownStationId());
            ps.setInt(4, sectionEntity.getDistance());
            return ps;
        }, keyHolder);

        long newId = keyHolder.getKey().longValue();
        return new SectionEntity(newId, sectionEntity.getLineId(), sectionEntity.getUpStationId(), sectionEntity.getDownStationId(), sectionEntity.getDistance());
    }

    @Override
    public List<SectionEntity> findAll() {
        return null;
    }

    @Override
    public Optional<SectionEntity> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<SectionEntity> findAllByLineId(Long lineId) {
        String sql = "SELECT * FROM SECTION WHERE line_id = ?";
        List<SectionEntity> sectionEntities = jdbcTemplate.query(sql, sectionEntityRowMapper, lineId);
        if (sectionEntities.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] 노선에 구간이 등록되어 있지 않습니다.");
        }
        return sectionEntities;
    }
}
