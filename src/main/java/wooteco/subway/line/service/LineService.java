package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.station.dao.StationDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse save(LineCreateRequest lineCreateRequest) {
        validateDuplicateName(lineCreateRequest.getName());
        validateIfDownStationIsEqualToUpStation(lineCreateRequest);

        Line line = Line.of(lineCreateRequest);
        Line savedLine = lineDao.save(line);

        stationDao.findById(lineCreateRequest.getDownStationId())
                .orElseThrow(() -> new IllegalArgumentException("입력하신 하행역이 존재하지 않습니다."));
        stationDao.findById(lineCreateRequest.getUpStationId())
                .orElseThrow(() -> new IllegalArgumentException("입력하신 상행역이 존재하지 않습니다."));
        sectionDao.save(Section.of(savedLine.getId(), lineCreateRequest));
        return LineResponse.from(savedLine);
    }

    private void validateIfDownStationIsEqualToUpStation(LineRequest lineRequest) {
        if (lineRequest.isSameStations()) {
            throw new IllegalArgumentException("상행과 하행 종점은 같을 수 없습니다.");
        }
    }

    public Line lineRequestToLine(LineRequest lineRequest) {
        stationDao.findById(lineRequest.getDownStationId()).orElseThrow(() -> new IllegalArgumentException("입력하신 하행역이 존재하지 않습니다."));
        stationDao.findById(lineRequest.getUpStationId()).orElseThrow(() -> new IllegalArgumentException("입력하신 상행역이 존재하지 않습니다."));

        return new Line(lineRequest.getName(), lineRequest.getColor());
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse find(Long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 노선이 존재하지 않습니다."));
        List<Section> sectionsByLineId = sectionDao.findAllByLineId(line.getId());
        LineRoute lineRoute = new LineRoute(sectionsByLineId);
        List<StationResponse> stations = lineRoute.getOrderedStations()
                .stream()
                .map(stationDao::findById)
                .map(Optional::get)
                .map(StationResponse::of)
                .collect(Collectors.toList());
        return LineResponse.of(line, stations);
    }

    public void delete(Long id) {
        lineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("삭제하려는 노선이 존재하지 않습니다"));
        lineDao.delete(id);
    }

    public void update(Long id, LineUpdateRequest lineUpdateRequest) {
        lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정하려는 노선이 존재하지 않습니다"));
        validateDuplicateNameExceptMyself(id, lineUpdateRequest.getName());
        Line line = Line.of(id, lineUpdateRequest);
        lineDao.update(line);
    }

    private void validateDuplicateName(String lineName) {
        if (lineDao.findByName(lineName).isPresent()) {
            throw new IllegalArgumentException("같은 이름의 노선이 있습니다;");
        }
    }
}
