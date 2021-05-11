package wooteco.subway.line.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.*;

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
        Line savedLine = lineDao.save(line);
        sectionDao.saveAll(savedLine.getId(), savedLine.getSections());
        return savedLine;
    }

    @Override
    public Lines findAll() {
        return new Lines(lineDao.allLines());
    }

    @Override
    public Line findById(final Long id) {
        Line line = lineDao.findById(id);
        List<Section> sections = sectionDao.findAll(id);
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

    @Override
    public void addSection(final Long id, final Section section) {
        sectionDao.save(id, section);
    }

    @Override
    public void deleteSection(final Long id, final Section section) {
        sectionDao.delete(id, section);
    }
}
