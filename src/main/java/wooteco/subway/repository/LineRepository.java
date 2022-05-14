package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
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

    public Line save(Line line) {
        final LineEntity savedLine = lineDao.save(LineEntity.from(line));
        final List<Section> sections = line.getSections()
            .stream()
            .map(section -> sectionRepository.save(savedLine.getId(), section))
            .collect(Collectors.toList());
        return new Line(savedLine.getId(), savedLine.getName(), savedLine.getColor(), sections);
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

    public void update(Line line) {
        final boolean isUpdated = lineDao.update(LineEntity.fromWithId(line));
        if (!isUpdated) {
            throw new RowNotFoundException("수정하고자 하는 노선이 존재하지 않습니다.");
        }
    }

    public void delete(Long id) {
        final boolean isDeleted = lineDao.delete(id);
        if (!isDeleted) {
            throw new RowNotFoundException("삭제하고자 하는 노선이 존재하지 않습니다.");
        }
    }
}
