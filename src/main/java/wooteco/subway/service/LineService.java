package wooteco.subway.service;

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
import wooteco.subway.exception.DuplicateNameException;

import java.util.List;
import java.util.stream.Collectors;
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

        Station upStation = getStationOrException(lineRequest.getUpStationId());
        Station downStation = getStationOrException(lineRequest.getDownStationId());

        try {
            Line line = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
            sectionDao.save(new Section(lineRequest.getUpStationId(), lineRequest.getDownStationId(), line.getId(),
                    lineRequest.getDistance()));

            List<StationResponse> stations = List.of(upStation, downStation)
                    .stream()
                    .map(StationResponse::new)
                    .collect(Collectors.toList());

            return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
        } catch (DuplicateKeyException e) {
            throw new DuplicateNameException(lineRequest.getName() + "은 이미 존재합니다.");
        }
    }

    private Station getStationOrException(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NotFoundException(stationId + "에 해당하는 지하철 노선을 찾을 수 없습니다."));
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(l -> new LineResponse(l.getId(), l.getName(), l.getColor(), null))
                .collect(Collectors.toList());
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
            throw new DuplicateNameException(name + "은 이미 존재합니다.");
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
