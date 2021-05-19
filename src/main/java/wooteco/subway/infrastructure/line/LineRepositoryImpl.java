package wooteco.subway.infrastructure.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineRepository;
import wooteco.subway.domain.line.section.Section;
import wooteco.subway.domain.line.section.Sections;
import wooteco.subway.domain.line.value.line.LineColor;
import wooteco.subway.domain.line.value.line.LineId;
import wooteco.subway.domain.line.value.line.LineName;
import wooteco.subway.domain.line.value.section.Distance;
import wooteco.subway.domain.line.value.section.SectionId;
import wooteco.subway.domain.station.value.StationId;

import java.util.List;
import java.util.Objects;

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
                        new LineId(savedLine.getLineId()),
                        new StationId(section.getUpStationId()),
                        new StationId(section.getDownStationId()),
                        new Distance(section.getDistance())
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
        Line originLine = findById(line.getLineId());
        List<Section> originSections = originLine.getSections();
        List<Section> changedSections = line.getSections();

        for (Section changedSection : changedSections) {
            originSections.removeIf(section -> section.hasSameId(changedSection));
        }

        List<Section> deletedSections = originSections;

        for (Section originSection : originLine.getSections()) {
            changedSections.removeIf(section ->
                    Objects.equals(section, originSection));
        }

        List<Section> creatableSection = changedSections.stream()
                .filter(section -> section.hasSameId(SectionId.empty()))
                .collect(toList());

        List<Section> updatableSections = changedSections.stream()
                .filter(section -> !section.hasSameId(SectionId.empty()))
                .collect(toList());

        lineDao.update(line);
        sectionDao.delete(deletedSections);
        sectionDao.update(updatableSections);
        sectionDao.save(creatableSection);
    }

    @Override
    public void deleteById(final Long id) {
        List<Section> sections = sectionDao.findAllByLineId(id);
        sectionDao.delete(sections);

        lineDao.deleteById(id);
    }

    @Override
    public boolean contains(Line line) {
        return lineDao.contains(line);
    }

}
