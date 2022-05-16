package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.SubwayException;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
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
            throw new SubwayException("[ERROR] 노선의 종점들은 존재하는 역이어야 합니다.");
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
            throw new SubwayException("[ERROR] 유효한 id가 아닙니다.");
        }
    }

    public void deleteById(Long id) {
        validateId(id);
        lineDao.deleteById(id);
        sectionDao.deleteByLineId(id);
    }

    private List<Station> generateStations(Long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        List<Long> totalIds = sections.getStations();
        List<Station> result = new ArrayList<>();
        for (Long stationId : totalIds) {
            result.add(stationDao.findById(stationId));
        }
        return result;
    }
}
