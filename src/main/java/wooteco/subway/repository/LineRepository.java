package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineSeries;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.exception.RowNotFoundException;
import wooteco.subway.util.SimpleReflectionUtils;

@Repository
public class LineRepository {

    private final PersistManager<LineEntity> persistManager;
    private final SectionRepository sectionRepository;
    private final LineDao lineDao;

    public LineRepository(PersistManager<LineEntity> persistManager,
        SectionRepository sectionRepository,
        LineDao lineDao) {
        this.persistManager = persistManager;
        this.sectionRepository = sectionRepository;
        this.lineDao = lineDao;
    }

    public void persist(LineSeries lineSeries) {
        final List<Long> persistedIds = toIds(findAllLines());
        final List<Line> lines = lineSeries.getLines();
        for (Line line : lines) {
            LineEntity entity = LineEntity.from(line);
            Long id = persistManager.persist(lineDao, entity, persistedIds);
            persistedIds.remove(id);
            SimpleReflectionUtils.injectId(line, id);
            sectionRepository.persist(id, line.getSectionSeries());
        }
        persistManager.deletePersistedAll(lineDao, persistedIds);
    }

    private List<Long> toIds(List<Line> lines) {
        return lines.stream()
            .map(Line::getId)
            .collect(Collectors.toList());
    }

    private void persistEach(List<Line> lines, List<Long> persistedIds) {
        for (Line line : lines) {
            LineEntity entity = LineEntity.from(line);
            Long id = persistManager.persist(lineDao, entity, persistedIds);
            SimpleReflectionUtils.injectId(line, id);
            sectionRepository.persist(id, line.getSectionSeries());
        }
    }

    public List<Line> findAllLines() {
        return lineDao.findAll()
            .stream()
            .map(entity -> new Line(entity.getId(),
                entity.getName(),
                entity.getColor(),
                sectionRepository.findAllSections(entity.getId())))
            .collect(Collectors.toList());
    }

    public Line findById(Long id) {
        final LineEntity entity = lineDao.findById(id)
            .orElseThrow(() -> new RowNotFoundException("조회하고자 하는 노선이 존재하지 않습니다."));
        return new Line(entity.getId(), entity.getName(), entity.getColor(), sectionRepository.findAllSections(id));
    }
}
