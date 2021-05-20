package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    @Autowired
    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public Line createLine(Line line, Section section) {
        long lineId = lineDao.save(line);
        Section newSection = new Section(new Line(lineId), section.getUpStation(), section.getDownStation(), section.getDistance());
        sectionDao.save(newSection);

        return new Line(lineId, line.getName(), line.getColor(), Collections.emptyList());
    }

    public List<Line> showLines() {
        return lineDao.findAll();
    }

    public Line showLineInfo(long lineId) {
        Map<Long, Long> sectionMap = sectionDao.sectionMap(lineId);
        long upStation = findUpStation(sectionMap);
        List<Station> stations = getStations(sectionMap, upStation);

        Line line = lineDao.findById(lineId);

        return new Line(lineId, line.getName(), line.getColor(), stations);
    }

    public void updateLine(long id, Line newLine) {
        Line line = lineDao.findById(id);
        lineDao.update(line.update(newLine.getName(), newLine.getColor()));
    }

    public void deleteLine(long id) {
        Line line = lineDao.findById(id);
        lineDao.delete(line);
    }

    private long findUpStation(Map<Long, Long> sectionMap) {
        Set<Long> upStationSet = new HashSet<>(sectionMap.keySet());
        Set<Long> downStationSet = new HashSet<>(sectionMap.values());
        upStationSet.removeAll(downStationSet);
        return upStationSet.iterator().next();
    }

    private List<Station> getStations(Map<Long, Long> sectionMap, long upStation) {
        List<Long> stationsId = new ArrayList<>();
        stationsId.add(upStation);
        long key = upStation;

        while (sectionMap.containsKey(key)) {
            key = sectionMap.get(key);
            stationsId.add(key);
        }

        return stationsId.stream()
                .map(stationDao::findById)
                .collect(Collectors.toList());
    }

}
