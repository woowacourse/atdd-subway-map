package wooteco.subway.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import java.util.List;

@Repository
public class LineRepository {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineRepository(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public long save(Line line) {
        return lineDao.save(line);
    }

    public List<Line> findAll() {
        List<Line> lines = lineDao.findAll();
        lines.forEach(this::addSections);
        return lines;
    }

    private void addSections(Line line) {
        long id = line.getId();
        List<Section> foundSections = sectionDao.findAllByLineId(id);
        foundSections.forEach(this::addStations);
        Sections sections = new Sections(foundSections);
        line.setSections(sections);
    }

    private void addStations(Section section) {
        long id = section.getId();
        List<Long> stationIds = sectionDao.findStationIdsById(id);
        long upStationId = stationIds.get(0);
        long downStationId = stationIds.get(1);
        Station upStation = findStationById(upStationId);
        Station downStation = findStationById(downStationId);
        section.setUpStation(upStation);
        section.setDownStation(downStation);
    }

    private Station findStationById(long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new SubwayException(ExceptionStatus.ID_NOT_FOUND));
    }

    public Line findById(long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new SubwayException(ExceptionStatus.ID_NOT_FOUND));
        addSections(line);
        return line;
    }

    public void update(long id, String name, String color) {
        Line line = new Line(id, name, color);
        lineDao.update(line);
    }

    public void deleteById(long id) {
        lineDao.deleteById(id);
    }
}
