package wooteco.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineResponse;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao<Section> sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao<Section> sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse save(String name, String color, Long upStationId, Long downStationId, int distance) {
        checkExistsName(name);
        Line savedLine = lineDao.save(new Line(name, color));
        Station upStation = stationDao.findById(upStationId);
        Station downStation = stationDao.findById(downStationId);
        sectionDao.save(new Section(savedLine, upStation, downStation, distance));
        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor(),
                List.of(upStation, downStation));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), getStations(line.getId())))
                .collect(Collectors.toList());
    }

    private List<Station> getStations(Long lineId) {
        return stationDao.findByLineId(lineId);
    }

    @Transactional(readOnly = true)
    public LineResponse showLine(Long id) {
        checkExistsId(id);
        Line line = lineDao.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), getStations(line.getId()));
    }

    @Transactional
    public int updateLine(Long id, String name, String color) {
        checkExistsId(id);
        checkExistsName(name);
        return lineDao.updateLineById(id, name, color);
    }

    @Transactional
    public int deleteLine(Long id) {
        checkExistsId(id);
        return lineDao.deleteById(id);
    }

    private void checkExistsName(String name) {
        if (lineDao.existsByName(name)) {
            throw new IllegalArgumentException("중복된 노선 이름입니다.");
        }
    }

    private void checkExistsId(Long id) {
        if (lineDao.notExistsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 id입니다.");
        }
    }
}
