package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dao.entity.LineEntity;
import wooteco.subway.dao.entity.SectionEntity;
import wooteco.subway.dao.entity.StationEntity;
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

        StationEntity upStation = getStationOrException(lineRequest.getUpStationId());
        StationEntity downStation = getStationOrException(lineRequest.getDownStationId());

        try {
            LineEntity lineEntity = lineDao.save(new LineEntity(lineRequest.getName(), lineRequest.getColor()));
            sectionDao.save(
                    new SectionEntity(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineEntity.getId(),
                            lineRequest.getDistance()));

            List<StationResponse> stations = List.of(upStation, downStation)
                    .stream()
                    .map(StationResponse::new)
                    .collect(Collectors.toList());

            return new LineResponse(lineEntity, stations);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("이미 존재하는 이름 또는 색깔이 있습니다.");
        }
    }

    private StationEntity getStationOrException(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NotFoundException(stationId + "에 해당하는 지하철 노선을 찾을 수 없습니다."));
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(l -> new LineResponse(l, null))
                .collect(Collectors.toList());
    }

//    public List<LineResponse> findAll() {
//        List<LineResponse> lineResponses = new ArrayList<>();
//        for (LineEntity lineEntity : lineDao.findAll()) {
//            List<Section> sections = sectionDao.findAllByLineId(lineEntity.getId())
//                    .stream()
//                    .map(s -> new Section(s.getDistance(), stationDao.findById(s.getUpstationid()),
//                            stationDao.findById(s.getDownStationId())))
//                    .collect(Collectors.toList());
//            Sections sections = new Sections(sections);
//            lines.add(new Line(lineEntity, sections.getOrderedStations()));
//        }
//
//        return lineResponses;
//    }

    public LineResponse findById(Long lineId) {
        LineEntity lineEntity = getLineOrThrowException(lineId);
        return new LineResponse(lineEntity, null);
    }

    public void update(Long lineId, String name, String color) {
        getLineOrThrowException(lineId);
        try {
            lineDao.update(new LineEntity(lineId, name, color));
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("이미 존재하는 노선 이름 또는 색깔이 있습니다.");
        }
    }

    public void delete(Long lineId) {
        getLineOrThrowException(lineId);
        lineDao.delete(lineId);
    }

    private LineEntity getLineOrThrowException(Long lineId) {
        return lineDao.findById(lineId)
                .orElseThrow(() -> new NotFoundException(lineId + "에 해당하는 지하철 노선을 찾을 수 없습니다."));
    }
}
