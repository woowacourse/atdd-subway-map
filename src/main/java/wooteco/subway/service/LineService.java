package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import wooteco.subway.exception.DuplicateNameException;
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

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        try {
            Line line = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
            Station upStation = getStation(lineRequest.getUpStationId());
            Station downStation = getStation(lineRequest.getDownStationId());
            sectionDao.save(new Section(upStation, downStation, line, lineRequest.getDistance()));
            return new LineResponse(line.getId(), line.getName(), line.getColor(), toStationsResponse(lineRequest));
        } catch (DuplicateKeyException e) {
            throw new DuplicateNameException(lineRequest.getName() + "은 존재하는 노선입니다.");
        }
    }

    private List<StationResponse> toStationsResponse(LineRequest lineRequest) {
        return List.of(getStation(lineRequest.getUpStationId()),
                        getStation(lineRequest.getDownStationId()))
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
        return sections.getStations().stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private Station getStation(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NotFoundException(stationId + "에 해당하는 지하철 노선을 찾을 수 없습니다."));
    }

    public LineResponse findById(Long lineId) {
        Line line = getLine(lineId);
        return new LineResponse(line, findAllStationResponseByLineId(line.getId()));
    }

    @Transactional
    public void update(Long lineId, String name, String color) {
        getLine(lineId);
        try {
            lineDao.update(new Line(lineId, name, color));
        } catch (DuplicateKeyException e) {
            throw new DuplicateNameException(name + "은 존재하는 노선입니다.");
        }
    }

    @Transactional
    public void delete(Long lineId) {
        getLine(lineId);
        lineDao.delete(lineId);
        sectionDao.deleteByLineId(lineId);
    }

    private Line getLine(Long lineId) {
        return lineDao.findById(lineId)
                .orElseThrow(() -> new NotFoundException(lineId + "에 해당하는 지하철 노선을 찾을 수 없습니다."));
    }
}
