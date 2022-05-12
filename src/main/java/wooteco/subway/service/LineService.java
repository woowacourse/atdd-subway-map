package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.WooTecoException;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.MetroManager;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse save(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        if (!stationDao.isValidId(lineRequest.getUpStationId()) || !stationDao.isValidId(lineRequest.getDownStationId())) {
            throw new IllegalArgumentException();
        }
        Line newLine = lineDao.save(line);
        sectionDao.save(new Section(newLine.getId(), lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance()), newLine.getId());
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), generateStations(newLine.getId()));
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor(), generateStations(it.getId())))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        List<Station> totalStations = generateStations(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), totalStations);
    }

    public void changeLineName(Long id, String name) {
        validateId(id);
        lineDao.changeLineName(id, name);
    }

    private void validateId(Long id) {
        if (!lineDao.isValidId(id)) {
            throw new WooTecoException("[ERROR] 유효한 id가 아닙니다.");
        }
    }

    public void deleteById(Long id) {
        validateId(id);
        lineDao.deleteById(id);
        sectionDao.deleteByLineId(id);
    }

    private List<Station> generateStations(Long lineId) {
        MetroManager metroManager = new MetroManager(sectionDao.findAll(lineId));
        List<Long> totalIds = metroManager.getStationsId();
        List<Station> result = new ArrayList<>();
        for (Long stationId : totalIds) {
            result.add(stationDao.findById(stationId));
        }
        return result;
    }
}
