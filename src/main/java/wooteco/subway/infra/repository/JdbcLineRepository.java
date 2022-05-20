package wooteco.subway.infra.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.exception.SubwayUnknownException;
import wooteco.subway.exception.SubwayValidationException;
import wooteco.subway.infra.dao.LineDao;
import wooteco.subway.infra.dao.entity.LineEntity;

@Repository
public class JdbcLineRepository implements LineRepository {

    private final LineDao lineDao;
    private final SectionRepository sectionRepository;

    public JdbcLineRepository(LineDao lineDao, SectionRepository sectionRepository) {
        this.lineDao = lineDao;
        this.sectionRepository = sectionRepository;
    }

    @Override
    public Line save(Line line) {
        final LineEntity savedLine = lineDao.save(new LineEntity(line.getName(), line.getColor()));
        final Sections sectionInput = line.getSections();
        final List<Section> sections = sectionInput.getSections();
        if (sections.size() != 1) {
            throw new SubwayValidationException("노선 등록을 위한 구간 등록 중 예외가 발생했습니다");
        }
        final Section section = sections.get(0);
        sectionRepository.save(savedLine.getId(), section);

        return new Line(savedLine.getId(), savedLine.getName(), savedLine.getColor(), sectionInput);
    }

    @Override
    public List<Line> findAll() {
        final List<Line> lines = findAllLines();
        final List<Sections> sections = sectionRepository.findAll();

        return sections.stream()
                .map(oneSections -> Line.of(findLineBySections(lines, oneSections), oneSections))
                .collect(Collectors.toList());
    }

    private List<Line> findAllLines() {
        return lineDao.findAll()
                .stream()
                .map(entity -> new Line(entity.getId(), entity.getName(), entity.getColor()))
                .collect(Collectors.toList());
    }

    private Line findLineBySections(List<Line> lines, Sections oneSections) {
        return lines.stream()
                .filter(line -> oneSections.isSameLineId(line.getId()))
                .findAny()
                .orElseThrow(() -> new SubwayUnknownException("구간에 해당하는 노선을 찾지 못헀습니다"));
    }

    @Override
    public Optional<Line> findById(Long id) {
        final Optional<LineEntity> lineEntity = lineDao.findById(id);

        if (lineEntity.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(toLine(lineEntity.get()));
    }

    private Line toLine(LineEntity entity) {
        return new Line(entity.getId(), entity.getName(), entity.getColor());
    }

    @Override
    public boolean existByName(String name) {
        return lineDao.existsByName(name);
    }

    @Override
    public boolean existByColor(String color) {
        return lineDao.existsByColor(color);
    }

    @Override
    public boolean existSameNameWithDifferentId(String name, Long id) {
        return lineDao.existSameNameWithDifferentId(name, id);
    }

    @Override
    public boolean existById(Long id) {
        return lineDao.existsById(id);
    }

    @Override
    public long update(Line line) {
        final LineEntity lineEntity = new LineEntity(line.getId(), line.getName(), line.getColor());
        return lineDao.updateById(lineEntity);
    }

    @Override
    public long deleteById(Long id) {
        return lineDao.deleteById(id);
    }
}
