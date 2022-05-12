package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
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
        try {
            Line line = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
            sectionDao.save(
                    new Section(lineRequest.getUpStationId(), lineRequest.getDownStationId(), line.getId(),
                            lineRequest.getDistance()));
            return new LineResponse(line.getId(), line.getName(), line.getColor(), toStationsResponse(lineRequest));
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("이미 존재하는 이름 또는 색깔이 있습니다.");
        }
    }

    private List<StationResponse> toStationsResponse(LineRequest lineRequest) {
        return List.of(getStationOrException(lineRequest.getUpStationId()),
                        getStationOrException(lineRequest.getDownStationId()))
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll().stream()
                .map(l -> new LineResponse(l.getId(), l.getName(), l.getColor(),
                        findAllStationResponseByLineId(l.getId())))
                .collect(Collectors.toList());
    }

    public List<StationResponse> findAllStationResponseByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        return sections.getStationsId().stream()
                .map(this::getStationOrException)
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private Station getStationOrException(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NotFoundException(stationId + "에 해당하는 지하철 노선을 찾을 수 없습니다."));
    }

    public LineResponse findById(Long lineId) {
        Line line = getLineOrThrowException(lineId);
        return new LineResponse(line, findAllStationResponseByLineId(line.getId()));
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
        sectionDao.deleteByLineId(lineId);
    }

    private Line getLineOrThrowException(Long lineId) {
        return lineDao.findById(lineId)
                .orElseThrow(() -> new NotFoundException(lineId + "에 해당하는 지하철 노선을 찾을 수 없습니다."));
    }
}
