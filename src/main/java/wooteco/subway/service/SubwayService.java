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

    // TODO: 새로 들어가는 역이 상행역인 경우 처리 안했음
    //  section 추가하기 전에 db 수정해놓는 메서드 ( 수정 전에 예외 처리 )
    public void updateSection(long lineId, Section section) {
        Sections sections = new Sections(sectionDao.selectAll(lineId));
        Line line = lineDao.select(lineId);
        sections.validateIfPossibleToInsert(section, line.getUpwardTerminalId(), line.getDownwardTerminalId());

        if (isSideInsertion(section, line)) {
            processSideInsertion(lineId, line, section);
            return;
        }
        sectionDao.update(lineId, section);
    }

    private boolean isSideInsertion(Section section, Line line) {
        if (section.getDownStationId() == line.getUpwardTerminalId()) {
            return true;
        }
        return section.getUpStationId() == line.getDownwardTerminalId();
    }

    private void processSideInsertion(long lineId, Line line, Section section) {
        if (section.getDownStationId() == line.getUpwardTerminalId()) {
            lineDao.updateUpwardTerminalId(lineId, section.getUpStationId());
        }

        if (section.getUpStationId() == line.getDownwardTerminalId()) {
            lineDao.updateDownwardTerminalId(lineId, section.getDownStationId());
        }
    }
}
