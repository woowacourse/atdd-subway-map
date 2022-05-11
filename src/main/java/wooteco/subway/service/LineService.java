package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.BadRequestLineException;
import wooteco.subway.exception.NotFoundException;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse save(LineRequest lineRequest) {
        if (lineRequest.getName().isBlank()) {
            throw new BadRequestLineException("이름은 공백, 빈값이면 안됩니다.");
        }

        if (lineRequest.getColor().isBlank()) {
            throw new BadRequestLineException("색깔은 공백, 빈값이면 안됩니다.");
        }

        if (lineRequest.getUpStationId() == lineRequest.getDownStationId()) {
            throw new BadRequestLineException("상행선과 하행선은 같은 지하철 역이면 안됩니다.");
        }

        if (lineRequest.getDistance() < 1) {
            throw new BadRequestLineException("상행선과 하행선의 거리는 1 이상이어야 합니다.");
        }

        Station upStation = getStationOrException(lineRequest.getUpStationId());
        Station downStation = getStationOrException(lineRequest.getDownStationId());

        try {
            Line line = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
            sectionDao.save(
                    new Section(lineRequest.getUpStationId(), lineRequest.getDownStationId(), line.getId(),
                            lineRequest.getDistance()));

            List<StationResponse> stations = List.of(upStation, downStation)
                    .stream()
                    .map(StationResponse::new)
                    .collect(Collectors.toList());

            return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("이미 존재하는 이름 또는 색깔이 있습니다.");
        }
    }

    private Station getStationOrException(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NotFoundException(stationId + "에 해당하는 지하철 노선을 찾을 수 없습니다."));
    }

    public List<LineResponse> findAll() {
        List<LineResponse> lineResponses = new ArrayList<>();
        List<Line> lines = lineDao.findAll();
        for (Line line : lines) {
            List<Section> allByLineId = sectionDao.findAllByLineId(line.getId());
        }
        return lineResponses;
    }

    public LineResponse findById(Long lineId) {
        Line line = getLineOrThrowException(lineId);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), null);
    }

    public void update(Long lineId, String name, String color) {
        getLineOrThrowException(lineId);
        try {
            lineDao.update(new Line(lineId, name, color));
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("이미 존재하는 노선 이름 또는 색깔이 있습니다.");
        }
    }

    public void delete(Long lineId) {
        getLineOrThrowException(lineId);
        lineDao.delete(lineId);
    }

    private Line getLineOrThrowException(Long lineId) {
        return lineDao.findById(lineId)
                .orElseThrow(() -> new NotFoundException(lineId + "에 해당하는 지하철 노선을 찾을 수 없습니다."));
    }
}
