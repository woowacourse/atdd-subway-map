package wooteco.subway.repository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.LineSeries;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.exception.RowNotFoundException;

@Repository
public class LineRepository {

    private final SectionRepository sectionRepository;
    private final LineDao lineDao;

    public LineRepository(SectionRepository sectionRepository, LineDao lineDao) {
        this.sectionRepository = sectionRepository;
        this.lineDao = lineDao;
    }

    public void persist(LineSeries lineSeries) {
        final List<Line> lines = lineSeries.getLines();
        List<Long> persistedIds = toIds(findAllLines());

        deleteLines(lines, persistedIds);
        updateLines(lines, persistedIds);
    }

    private void updateLines(List<Line> lines, List<Long> persistedIds) {
        for (Line line : lines) {
            if (persistedIds.contains(line.getId())) {
                update(line);
                continue;
            }
            save(line);
        }
    }

    private void deleteLines(List<Line> lines, List<Long> persistedIds) {
        final List<Long> ids = toIds(lines);

        for (Long persistedId : persistedIds) {
            if (!ids.contains(persistedId)) {
                delete(persistedId);
            }
        }
    }

    private List<Long> toIds(List<Line> lines) {
        return lines.stream()
            .map(Line::getId)
            .collect(Collectors.toList());
    }

    private Line save(Line line) {
        final Long id = lineDao.save(LineEntity.from(line));
        sectionRepository.persist(id, line.getSectionSeries());
        return injectID(line, id);
    }

    private Line injectID(Line line, Long id) {
        final Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, id);
        return line;
    }

    public List<Line> findAllLines() {
        return lineDao.findAll()
            .stream()
            .map(entity -> new Line(entity.getId(),
                entity.getName(),
                entity.getColor(),
                sectionRepository.readAllSections(entity.getId())))
            .collect(Collectors.toList());
    }

    public Line findById(Long id) {
        final LineEntity entity = lineDao.findById(id)
            .orElseThrow(() -> new RowNotFoundException("조회하고자 하는 노선이 존재하지 않습니다."));
        return new Line(entity.getId(), entity.getName(), entity.getColor(), sectionRepository.readAllSections(id));
    }

    private void update(Line line) {
        final boolean isUpdated = lineDao.update(LineEntity.fromWithId(line));
        if (!isUpdated) {
            throw new RowNotFoundException("수정하고자 하는 노선이 존재하지 않습니다.");
        }
    }

    private Long delete(Long id) {
        final boolean isDeleted = lineDao.delete(id);
        if (!isDeleted) {
            throw new RowNotFoundException("삭제하고자 하는 노선이 존재하지 않습니다.");
        }
        return id;
    }
}
