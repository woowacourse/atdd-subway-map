package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubwayService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SubwayService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public long createLine(Line line) {
        long lineId = lineDao.insert(line);
        sectionDao.insert(lineId, new Section(line.getUpStationId(), line.getDownStationId(), line.getDistance()));
        return lineId;
    }

    public List<Line> showLines() {
        return lineDao.selectAll();
    }

    public Line showLineDetail(long id) {
        Line line = lineDao.select(id);
        return line;
    }

    public List<Station> getStationsInLine(long id) {
        List<Section> sections = sectionDao.selectAll(id);
        return sections.stream()
                .map(section -> stationDao.select(section.getUpStationId()))
                .collect(Collectors.toList());
    }

    public void modifyLine(long id, Line line) {
        lineDao.update(id, line);
    }

    public void deleteLine(long id) {
        lineDao.delete(id);
    }

    public long createStation(Station station) {
        return stationDao.insert(station);
    }

    public List<Station> showStations() {
        return stationDao.selectAll();
    }

    public void deleteStation(long id) {
        stationDao.delete(id);
    }

    public long createSection(long lineId, Section section) {
        return sectionDao.insert(lineId, section);
    }
}
