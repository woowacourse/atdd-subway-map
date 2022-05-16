package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.notfound.LineNotFoundException;
import wooteco.subway.exception.notfound.NotFoundException;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.entity.LineEntity;

@Repository
public class LineRepository {

    private final LineDao lineDao;
    private final SectionRepository sectionRepository;

    public LineRepository(LineDao lineDao, SectionRepository sectionRepository) {
        this.lineDao = lineDao;
        this.sectionRepository = sectionRepository;
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
                .map(this::toLine)
                .orElseThrow(NotFoundException::new);
    }

    private Line toLine(LineEntity entity) {
        return new Line(entity.getId(), entity.getName(), entity.getColor(),
                sectionRepository.findByLineId(entity.getId()));
    }

    public List<Line> findAll() {
        return lineDao.findAll().stream()
                .map(this::toLine)
                .collect(Collectors.toList());
    }

    public void update(Line line) {
        sectionRepository.deleteByLineId(line.getId());
        sectionRepository.saveAll(line.getSections());
        lineDao.update(LineEntity.from(line));
    }

    public Line save(Line line) {
        LineEntity saved = lineDao.save(LineEntity.from(line));
        List<Section> sections = line.getSections().stream()
                .map(section -> new Section(saved.getId(), section.getUpStation(), section.getDownStation(),
                        section.getDistance()))
                .collect(Collectors.toList());
        sectionRepository.saveAll(sections);
        return new Line(saved.getId(), saved.getName(), saved.getColor(), sections);
    }

    public void deleteById(Long id) {
        lineDao.findById(id)
                        .orElseThrow(LineNotFoundException::new);
        sectionRepository.deleteByLineId(id);
        lineDao.deleteById(id);
    }
}
