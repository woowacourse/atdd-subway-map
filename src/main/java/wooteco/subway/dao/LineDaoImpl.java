package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
public class LineDaoImpl implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Line line) {
        final String sql = "INSERT INTO LINE (name, color) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT l.id as line_id, l.name as line_name, l.color as line_color, "
            + "s.id as section_id, s.up_station_id, us.name as up_station_name, s.down_station_id, "
            + "ds.name as down_station_name, s.distance "
            + "FROM LINE as l "
            + "LEFT JOIN SECTION AS s ON s.line_id = l.id "
            + "LEFT JOIN STATION AS us ON us.id = s.up_station_id "
            + "LEFT JOIN STATION AS ds ON ds.id = s.down_station_id";

        List<LineSection> lineSections = jdbcTemplate.query(sql, joinRowMapper());
        Map<Line, List<LineSection>> groupByLine = lineSections.stream()
            .collect(Collectors.groupingBy(LineSection::getLine));
        List<Line> lines = groupByLine.keySet().stream()
            .map(line -> Line.from(line, toSections(groupByLine.get(line))))
            .collect(Collectors.toList());

        return lines;
    }

    private List<Section> toSections(List<LineSection> lineSections) {
        return lineSections.stream()
            .map(LineSection::getSection)
            .collect(Collectors.toList());
    }

    private RowMapper<LineSection> joinRowMapper() {
        return (resultSet, rowNum) -> {
            final Long lineId = resultSet.getLong("line_id");
            final String name = resultSet.getString("line_name");
            final String color = resultSet.getString("line_color");
            Line line = new Line(lineId, name, color);

            Section section = serializeSection(resultSet);
            return new LineSection(line, section);
        };
    }

    private Section serializeSection(ResultSet resultSet) throws SQLException {
        final Long sectionId = resultSet.getLong("section_id");
        final Long upStationId = resultSet.getLong("up_station_id");
        final Long downStationId = resultSet.getLong("down_station_id");
        final String upStationName = resultSet.getString("up_station_name");
        final String downStationName = resultSet.getString("down_station_name");
        final int distance = resultSet.getInt("distance");

        return new Section(sectionId, new Station(upStationId, upStationName),
            new Station(downStationId, downStationName), distance);
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM LINE WHERE id = ?";
        int updateSize = jdbcTemplate.update(sql, id);
        return updateSize != 0;
    }

    @Override
    public Optional<Line> findById(Long id) {
        final String sql = "SELECT l.id as line_id, l.name as line_name, l.color as line_color, "
            + "s.id as section_id, s.up_station_id, us.name as up_station_name, s.down_station_id, "
            + "ds.name as down_station_name, s.distance "
            + "FROM LINE as l "
            + "LEFT JOIN SECTION AS s ON s.line_id = l.id "
            + "LEFT JOIN STATION AS us ON us.id = s.up_station_id "
            + "LEFT JOIN STATION AS ds ON ds.id = s.down_station_id "
            + "WHERE l.id = ?";
        try {
            List<LineSection> lineSections = jdbcTemplate.query(sql, joinRowMapper(), id);
            if (lineSections.isEmpty()) {
                return Optional.empty();
            }
            LineSection lineSection = lineSections.get(0);
            Line findLine = lineSection.getLine();
            List<Section> sections = lineSections.stream()
                .map(LineSection::getSection)
                .collect(Collectors.toList());
            return Optional.of(Line.from(findLine, sections));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public boolean updateById(Long id, Line line) {
        final String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        int updateSize = jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
        return updateSize != 0;
    }

    private static class LineSection {
        private final Line line;
        private final Section section;

        public LineSection(Line line, Section section) {
            this.line = line;
            this.section = section;
        }

        public Line getLine() {
            return line;
        }

        public Section getSection() {
            return section;
        }
    }
}
