package wooteco.subway.line.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;
import wooteco.subway.line.domain.section.Section;
import wooteco.subway.line.domain.section.Sections;
import wooteco.subway.line.domain.value.LineColor;
import wooteco.subway.line.domain.value.LineId;
import wooteco.subway.line.domain.value.LineName;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
public class LineRepositoryImpl implements LineRepository {
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.lineDao = new LineDao(jdbcTemplate);
        this.sectionDao = new SectionDao(jdbcTemplate);
    }

    @Override
    public Line save(Line line) {
        Line savedLine = lineDao.save(line);

        List<Section> sections = savedLine.getSections().stream().map(
                section -> new Section(
                        savedLine.getLineId(),
                        section.getUpStationId(),
                        section.getDownStationId(),
                        section.getDistance()
                )
        ).collect(toList());

        List<Section> savedSections = sectionDao.save(sections);

        return new Line(
                new LineId(savedLine.getLineId()),
                new LineName(savedLine.getLineName()),
                new LineColor(savedLine.getLineColor()),
                new Sections(savedSections)
        );
    }

    @Override
    public List<Line> allLines() {
        return lineDao.allLines();
    }

    @Override
    public Line findById(final Long id) {
        return lineDao.findById(id);
    }

    @Override
    public void update(Line line) {
        lineDao.update(line);
    }

    @Override
    public void deleteById(final Long id) {
        lineDao.deleteById(id);
    }

}
