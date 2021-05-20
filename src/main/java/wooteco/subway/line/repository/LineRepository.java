package wooteco.subway.line.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;

import java.util.List;
import java.util.Optional;

@Repository
public class LineRepository {
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineRepository(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("[ERROR] 존재하지 않는 노선입니다."));
        line.initSections(sectionDao.findAllByLineId(id));
        return line;
    }

    public Optional<Line> findByName(String name) {
        return lineDao.findByName(name);
    }

    public void update(Line line) {
        lineDao.update(line);
    }

    public Line save(String name, String color, Long upStationId, Long downStationId, int distance) {
        Line line = lineDao.save(new Line(name, color));
        Section section = sectionDao.save(new Section(line.id(), upStationId, downStationId, distance));
        return new Line(line.id(), line.name(), line.color(), section);
    }

    public void updateSection(Long lineId, Section section) {
        sectionDao.update(new Section(section.id(), lineId, section.upStationId(), section.downStationId(), section.distance()));
    }

    public Section addSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        return sectionDao.save(new Section(lineId, upStationId, downStationId, distance));
    }

    public void deleteSection(Long lineId, Long stationId) {
        sectionDao.deleteByLineIdAndStationId(lineId, stationId);
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
