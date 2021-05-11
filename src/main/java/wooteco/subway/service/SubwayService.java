package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
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
        return lineDao.insert(line);
    }

    public List<Line> showLines() {
        return lineDao.selectAll();
    }

    public Line showLineDetail(long id) {
        Line line = lineDao.select(id);
        return line;
    }

    public List<Station> getStationsInLine(long id) {
        Sections sections = new Sections(sectionDao.selectAll(id));
        Line line = lineDao.select(id);
        List<Long> stationIds = sections.getStationIds(line.getUpwardTerminalId(), line.getDownwardTerminalId());

        return stationIds.stream()
                .map(stationId -> stationDao.select(stationId))
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

    public void updateSection(long lineId, Section section) {
        int affectedRow = sectionDao.update(lineId, section);
        if (affectedRow == 0) {
            Line line = lineDao.select(lineId);
            processSideInsertion(lineId, section, line);
        }
    }

    private void processSideInsertion(long lineId, Section section, Line line) {
        if (section.getDownStationId() == line.getUpwardTerminalId()) {
            lineDao.updateUpwardTerminalId(lineId, section.getUpStationId());
        }

        if (section.getUpStationId() == line.getDownwardTerminalId()) {
            lineDao.updateDownwardTerminalId(lineId, section.getDownStationId());
        }
    }
}
