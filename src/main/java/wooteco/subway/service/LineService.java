package wooteco.subway.service;

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
import wooteco.subway.exception.ExistKeyException;
import wooteco.subway.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse create(LineRequest request) {
        validateExistLineName(request.getName());
        Line line = new Line(request.getName(), request.getColor());
        Line savedLine = lineDao.insert(line);

        validateExistStation(request.getUpStationId(), request.getDownStationId());
        Section section = new Section(savedLine.getId(),
                request.getUpStationId(), request.getDownStationId(), request.getDistance());
        sectionDao.insert(section);

        List<StationResponse> stationResponses = finAllStationsByLineId(savedLine);
        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor(), stationResponses);
    }

    private void validateExistLineName(String name) {
        if (lineDao.existLineByName(name)) {
            throw new ExistKeyException("요청하신 노선의 이름은 이미 존재합니다.");
        }
    }

    private void validateExistStation(Long upStationId, Long downStationId) {
        if (!stationDao.existStationById(upStationId) || !stationDao.existStationById(downStationId)) {
            throw new NotFoundException("등록되지 않은 역으로는 구간을 만들 수 없습니다.");
        }
    }

    private List<StationResponse> finAllStationsByLineId(Line savedLine) {
        List<Station> stations = stationDao.findAllByLineId(savedLine.getId());
        return stations.stream()
                .distinct()
                .map(s -> new StationResponse(s.getId(), s.getName()))
                .collect(Collectors.toList());
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(l -> LineResponse.of(l, createStationResponseByLineId(l.getId())))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long lineId) {
        Line line = lineDao.findById(lineId);
        List<StationResponse> stationResponses = createStationResponseByLineId(lineId);
        return LineResponse.of(line, stationResponses);
    }

    private List<StationResponse> createStationResponseByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        List<Long> sortedStationIds = sections.getSortedStationIds();
        List<Station> stations = stationDao.findAllByLineId(lineId);

        return convertSortedStationIdToResponses(sortedStationIds, stations);
    }

    private List<StationResponse> convertSortedStationIdToResponses(List<Long> sortedStationIds, List<Station> stations) {
        List<StationResponse> sortedStation = new ArrayList<>();
        for (Long stationId : sortedStationIds) {
            Optional<StationResponse> station = stations.stream()
                    .filter(s -> s.getId().equals(stationId))
                    .map(s -> new StationResponse(s.getId(), s.getName()))
                    .findFirst();
            station.ifPresent(sortedStation::add);
        }
        return sortedStation;
    }

    public void update(Long lineId, LineRequest request) {
        validateExistData(lineId);
        lineDao.update(new Line(lineId, request.getName(), request.getColor()));
    }

    public void delete(Long lineId) {
        validateExistData(lineId);
        lineDao.delete(lineId);
    }

    private void validateExistData(Long lineId) {
        boolean isExisted = lineDao.existLineById(lineId);
        if (!isExisted) {
            throw new NotFoundException("접근하려는 노선이 존재하지 않습니다.");
        }
    }
}
