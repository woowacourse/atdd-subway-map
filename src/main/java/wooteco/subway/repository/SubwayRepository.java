package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineInfo;
import wooteco.subway.domain.section.Section;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.exception.ExceptionType;
import wooteco.subway.exception.NotFoundException;

@Repository
public class SubwayRepository {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public SubwayRepository(LineDao lineDao,
                            SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public List<LineInfo> findAllLines() {
        return lineDao.findAll()
                .stream()
                .map(LineEntity::toDomain)
                .collect(Collectors.toList());
    }

    public List<Section> findAllSections() {
        return sectionDao.findAll()
                .stream()
                .map(SectionEntity::toDomain)
                .collect(Collectors.toList());
    }

    public List<Section> findAllSectionsByLineId(Long lineId) {
        return sectionDao.findAllByLineId(lineId)
                .stream()
                .map(SectionEntity::toDomain)
                .collect(Collectors.toList());
    }

    public List<Section> findAllSectionsByStationId(Long stationId) {
        return sectionDao.findAllByStationId(stationId)
                .stream()
                .map(SectionEntity::toDomain)
                .collect(Collectors.toList());
    }

    public LineInfo findExistingLine(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionType.LINE_NOT_FOUND))
                .toDomain();
    }

    public boolean checkExistingLine(Long id) {
        return lineDao.findById(id).isPresent();
    }

    public boolean checkExistingLineName(String name) {
        return lineDao.findByName(name).isPresent();
    }

    public Line saveLine(LineInfo lineInfo, Section section) {
        LineEntity lineEntity = lineDao.save(new LineEntity(lineInfo.getName(), lineInfo.getColor()));
        sectionDao.save(SectionEntity.of(lineEntity.getId(), section));
        return Line.of(lineEntity.toDomain(), section);
    }

    public void saveSections(Long lineId, List<Section> sections) {
        for (Section section : sections) {
            sectionDao.save(SectionEntity.of(lineId, section));
        }
    }

    public void updateLine(LineInfo lineInfo) {
        LineEntity updatedLine = new LineEntity(lineInfo.getId(), lineInfo.getName(), lineInfo.getColor());
        lineDao.update(updatedLine);
    }

    public void deleteLine(LineInfo lineInfo) {
        Long lineId = lineInfo.getId();
        lineDao.deleteById(lineId);
        sectionDao.deleteAllByLineId(lineId);
    }

    public void deleteSections(Long lineId, List<Section> sections) {
        for (Section section : sections) {
            sectionDao.delete(SectionEntity.of(lineId, section));
        }
    }
}
