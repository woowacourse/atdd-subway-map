package wooteco.subway.infra.repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.infra.dao.LineDao;
import wooteco.subway.infra.dao.SectionDao;
import wooteco.subway.infra.entity.LineEntity;

@Repository
public class JdbcLineRepository implements LineRepository {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public JdbcLineRepository(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Override
    public List<Line> findAll() {
        final List<LineEntity> lineEntities = lineDao.findAll();

        if (lineEntities.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Line> lines = lineEntities.stream()
                .map(this::toLine)
                .collect(Collectors.toList());

//        sectionDao.findAll()
//                .stream()
//                .map(entity -> new Section())

        return null;
    }

    private Line toLine(LineEntity entity) {
        return new Line(entity.getId(), entity.getName(), entity.getColor());
    }

    @Override
    public Optional<Line> findById(Long id) {
        final Optional<LineEntity> lineEntity = lineDao.findById(id);

        if (lineEntity.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(toLine(lineEntity.get()));
    }

    @Override
    public Line save(Line line) {
        return null;
    }

    @Override
    public boolean existByName(String name) {
        return false;
    }

    @Override
    public boolean existByColor(String color) {
        return false;
    }

    @Override
    public int update(Line line) {
        return 0;
    }

    @Override
    public int deleteById(Long id) {
        return 0;
    }
}
