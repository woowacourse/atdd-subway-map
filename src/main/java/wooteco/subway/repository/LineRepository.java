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
        LineEntity toSave = new LineEntity(null, line.getName(), line.getColor());
        LineEntity savedLine = lineDao.save(toSave);
        List<SectionEntity> savedSections = getSavedSections(line, savedLine);
        return convertToLine(savedLine, savedSections);
    }

    private List<SectionEntity> getSavedSections(Line line, LineEntity saved) {
        return line.getSections().stream()
                .map(section -> sectionDao.save(new SectionEntity(null, saved.getId(), section.getUpStationId(),
                        section.getDownStationId(), section.getDistance())))
                .collect(Collectors.toList());
    }

    private Line convertToLine(LineEntity line, List<SectionEntity> sectionEntities) {
        List<Section> sections = sectionEntities.stream()
                .map(entity -> new Section(entity.getId(), entity.getLine_id(), entity.getUpStationId(),
                        entity.getDownStationId(), entity.getDistance()))
                .collect(Collectors.toList());
        return new Line(line.getId(), line.getName(), line.getColor(), sections);
    }

    public List<Line> findAll() {
        return lineDao.findAll().stream()
                .map(entity -> convertToLine(entity, getSectionsFrom(entity)))
                .collect(Collectors.toList());
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
        LineEntity forUpdate = new LineEntity(line.getId(), line.getName(), line.getColor());
        LineEntity updatedEntity = lineDao.update(forUpdate);
        return convertToLine(updatedEntity, getSectionsFrom(updatedEntity));
    }

    public Integer deleteById(Long id) {
        //DELETE SECTIONS
        return lineDao.deleteById(id);
    }
}
