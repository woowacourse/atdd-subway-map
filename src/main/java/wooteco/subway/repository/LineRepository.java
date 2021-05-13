package wooteco.subway.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Sections;

@Repository
@RequiredArgsConstructor
public class LineRepository {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public Optional<Line> findCompleteLineById(Long lineId) {
        final Optional<Line> foundLine = lineDao.findLineById(lineId);
        foundLine.ifPresent(
            line -> line.addSections(Sections.create(sectionDao.findAllByLineId(lineId)))
        );
        return foundLine;
    }

    public Optional<Line> findLineByName(String name) {
        return lineDao.findLineByName(name);
    }

    public Line save(Line line) {
        return lineDao.save(line);
    }

    public Optional<Line> findLineByNameOrColor(String name, String color, Long id) {
        return lineDao.findLineByNameOrColor(name, color, id);
    }

    public void update(Line line) {
        lineDao.update(line);
    }

    public void removeLine(Long id) {
        lineDao.removeLine(id);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }
}
