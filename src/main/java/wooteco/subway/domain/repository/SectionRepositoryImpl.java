package wooteco.subway.domain.repository;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.utils.exception.NotFoundException;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class SectionRepositoryImpl implements SectionRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final StationRepository stationRepository;

    private RowMapper<Section> rowMapper() {
        return (resultSet, rowNum) -> {
            long id = resultSet.getLong("id");
            long lineId = resultSet.getLong("line_id");
            long upStationId = resultSet.getLong("up_station_id");
            long downStationId = resultSet.getLong("down_station_id");
            int distance = resultSet.getInt("distance");

            Station upStation = stationRepository.findById(upStationId)
                    .orElseThrow(() -> new NotFoundException("[ERROR] 상행역을 찾을 수 없습니다."));
            Station downStation = stationRepository.findById(downStationId)
                    .orElseThrow(() -> new NotFoundException("[ERROR] 하행역을 찾을 수 없습니다."));

            return new Section(id, lineId, upStation, downStation, distance);
        };
    }

    public SectionRepositoryImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
        this.stationRepository = new StationRepositoryImpl(dataSource);
    }

    @Override
    public Section save(Section section) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("line_id", section.getLineId())
                .addValue("up_station_id", section.getUpStation().getId())
                .addValue("down_station_id", section.getDownStation().getId())
                .addValue("distance", section.getDistance());

        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Section(
                id,
                section.getLineId(),
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance()
        );

    }

    @Override
    public List<Section> findAllByLineId(Long lineId) {
        String sql = "SELECT * FROM section WHERE line_id = :line_id";
        SqlParameterSource parameters = new MapSqlParameterSource("line_id", lineId);
        return namedParameterJdbcTemplate.query(sql, parameters, rowMapper());
    }
}
