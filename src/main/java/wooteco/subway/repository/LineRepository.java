package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.exception.LineNotFoundException;

@Repository
public class LineRepository {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineRepository(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Line save(Line line) {
        LineEntity toSave = LineEntity.from(line);
        LineEntity savedLine = lineDao.save(toSave);
        saveSections(savedLine.getId(), line.getSections());
        return findById(savedLine.getId());
    }

    private void saveSections(Long lineId, List<Section> sections) {
        for (Section section : sections) {
            SectionEntity entity = SectionEntity.of(lineId, section);
            sectionDao.save(entity);
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll().stream()
                .map(entity -> convertToLine(entity, getSectionsFrom(entity)))
                .collect(Collectors.toList());
    }

    private Line convertToLine(LineEntity line, List<SectionEntity> sectionEntities) {
        List<Section> sections = sectionEntities.stream()
                .map(entity -> new Section(entity.getId(), entity.getLine_id(), entity.getUpStationId(),
                        entity.getDownStationId(), entity.getDistance()))
                .collect(Collectors.toList());
        return new Line(line.getId(), line.getName(), line.getColor(), sections);
    }

    private List<SectionEntity> getSectionsFrom(LineEntity lineEntity) {
        return sectionDao.findByLineId(lineEntity.getId());
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
                .map(entity -> convertToLine(entity, getSectionsFrom(entity)))
                .orElseThrow(LineNotFoundException::new);
    }

    public Line update(Line line) {
        LineEntity forUpdate = LineEntity.from(line);
        LineEntity updatedEntity = lineDao.update(forUpdate);
        sectionDao.deleteByLineId(line.getId());
        return findById(updatedEntity.getId());
    }

    public Integer deleteById(Long id) {
        sectionDao.deleteByLineId(id);
        return lineDao.deleteById(id);
    }
}
