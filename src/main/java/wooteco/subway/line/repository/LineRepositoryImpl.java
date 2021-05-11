package wooteco.subway.line.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;

import java.util.List;

@Repository
public class LineRepositoryImpl implements LineRepository {
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineRepositoryImpl(final LineDao lineDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Override
    public Line save(final Line line) {
        return lineDao.save(line);
    }

    @Override
    public List<Line> findAll() {
        return lineDao.allLines();
    }

    @Override
    public Line findById(final Long id) {
        Line line = lineDao.findById(id);
        List<Section> sections = sectionDao.sections(id);
        return new Line(line.getId(), line.getName(), line.getColor(), new Sections(sections));
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
