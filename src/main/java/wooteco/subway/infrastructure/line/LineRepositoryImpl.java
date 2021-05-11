package wooteco.subway.infrastructure.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineRepository;
import wooteco.subway.domain.line.section.Section;
import wooteco.subway.domain.line.section.Sections;
import wooteco.subway.domain.line.value.LineColor;
import wooteco.subway.domain.line.value.LineId;
import wooteco.subway.domain.line.value.LineName;

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
                        section.getDownStationId(),
                        section.getUpStationId(),
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
        return lineDao.allLines().stream()
                .map(line -> new Line(
                        new LineId(line.getLineId()),
                        new LineName(line.getLineName()),
                        new LineColor(line.getLineColor()),
                        new Sections(sectionDao.findAllByLineId(line.getLineId()))
                )).collect(toList());
    }

    @Override
    public Line findById(final Long id) {
        Line line = lineDao.findById(id);
        List<Section> sections = sectionDao.findAllByLineId(line.getLineId());

        return new Line(
                new LineId(line.getLineId()),
                new LineName(line.getLineName()),
                new LineColor(line.getLineColor()),
                new Sections(sections)
        );
    }

    @Override
    public void update(Line line) {
        lineDao.update(line);
    }

    @Override
    public void deleteById(final Long id) {
        lineDao.deleteById(id);
    }

    @Override
    public boolean contains(Line line) {
        return lineDao.contains(line);
    }

}
