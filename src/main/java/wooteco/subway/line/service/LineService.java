package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.swing.SwingUtilities2.Section;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LineUpdateRequest;
import wooteco.subway.station.dao.StationDao;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;

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

        findStationByIdOrElseThrowException(lineCreateRequest.getDownStationId());
        findStationByIdOrElseThrowException(lineCreateRequest.getUpStationId);

        sectionDao.save(Section.of(savedLine.getId(), lineCreateRequest));
        return LineResponse.from(savedLine);
    }

    private void validateIfDownStationIsEqualToUpStation(LineRequest lineRequest) {
        if (lineRequest.isSameStations()) {
            throw new IllegalArgumentException("상행과 하행 종점은 같을 수 없습니다.");
        }
    }

    public Line lineRequestToLine(LineRequest lineRequest) {
        findStationByIdOrElseThrowException(lineRequest.getUpStationId());
        findStationByIdOrElseThrowException(lineRequest.getDownStationId());

        return new Line(lineRequest.getName(), lineRequest.getColor());
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse find(Long id) {
        Line line = findLineByIdOrElseThrowException(id);
        List<Section> sectionsByLineId = sectionDao.findAllByLineId(line.getId());
        LineRoute lineRoute = new LineRoute(sectionsByLineId);
        List<StationResponse> stations = lineRoute.getOrderedStations()
                .stream()
                .map(staitonId -> findStationByIdOrElseThrowException(staitonId))
                .map(StationResponse::of)
                .collect(Collectors.toList());
        return LineResponse.of(line, stations);
    }

    public void delete(Long id) {
        findLineByIdOrElseThrowException(id);
        lineDao.delete(id);
    }

    public void update(Long id, LineUpdateRequest lineUpdateRequest) {
        findLineByIdOrElseThrowException(id);
        validateDuplicateNameExceptMyself(id, lineUpdateRequest.getName());
        Line line = Line.of(id, lineUpdateRequest);
        lineDao.update(line);
    }

    private void validateDuplicateName(String lineName) {
        if (lineDao.findByName(lineName).isPresent()) {
            throw new IllegalArgumentException("같은 이름의 노선이 있습니다;");
        }
    }

    private Station findStationByIdOrElseThrowException(Long id) {
        return stationDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 지하철역이 존재하지 않습니다"));
    }

    private Line findLineByIdOrElseThrowException(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 노선이 존재하지 않습니다"));
    }
}
