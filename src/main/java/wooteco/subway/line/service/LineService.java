package wooteco.subway.line.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRoute;
import wooteco.subway.line.dto.LineCreateRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LineUpdateRequest;
import wooteco.subway.line.exception.LineIllegalArgumentException;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.exception.StationIllegalArgumentException;

@Service
public class LineService {
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse save(LineCreateRequest lineCreateRequest) {
        validateDuplicateName(lineCreateRequest.getName());
        validateAllStationsIsExist(lineCreateRequest);
        validateIfDownStationIsEqualToUpStation(lineCreateRequest);

        Line line = lineCreateRequest.toLine();
        Line savedLine = lineDao.save(line);

        sectionDao.save(lineCreateRequest.toSection(savedLine.getId()));
        return LineResponse.from(savedLine);
    }

    @Transactional
    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public LineResponse find(Long id) {
        Line line = findLineById(id);
        List<Section> sectionsByLineId = sectionDao.findAllByLineId(line.getId());
        LineRoute lineRoute = new LineRoute(sectionsByLineId);
        List<StationResponse> stations = lineRoute.getOrderedStations()
            .stream()
            .map(this::findStationById)
            .map(StationResponse::of)
            .collect(Collectors.toList());
        return LineResponse.of(line, stations);
    }

    @Transactional
    public void delete(Long id) {
        validateIsExistLineById(id);
        lineDao.delete(id);
    }

    @Transactional
    public void update(Long id, LineUpdateRequest lineUpdateRequest) {
        validateDuplicateNameExceptMyself(id, lineUpdateRequest.getName());
        Line line = lineUpdateRequest.toLine(id);
        lineDao.update(line);
    }

    private Station findStationById(Long id) {
        return stationDao.findById(id)
            .orElseThrow(() -> new StationIllegalArgumentException("해당 지하철 역이 존재하지 않습니다"));
    }

    private Line findLineById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new LineIllegalArgumentException("해당 노선이 존재하지 않습니다"));
    }

    private void validateDuplicateName(String lineName) {
        if (lineDao.findByName(lineName).isPresent()) {
            throw new LineIllegalArgumentException("같은 이름의 노선이 있습니다;");
        }
    }

    private void validateAllStationsIsExist(LineCreateRequest lineCreateRequest) {
        validateIsExistStationById(lineCreateRequest.getUpStationId());
        validateIsExistStationById(lineCreateRequest.getDownStationId());
    }

    private void validateIfDownStationIsEqualToUpStation(LineCreateRequest lineCreateRequest) {
        if (lineCreateRequest.isSameStations()) {
            throw new LineIllegalArgumentException("상행과 하행 종점은 같을 수 없습니다.");
        }
    }

    private void validateDuplicateNameExceptMyself(Long id, String lineName) {
        Optional<Line> lineByName = lineDao.findByName(lineName);
        if (lineByName.isPresent() && !lineByName.get().equalId(id)) {
            throw new LineIllegalArgumentException("같은 이름의 노선이 있습니다;");
        }
    }

    private void validateIsExistStationById(Long id) {
        if(!stationDao.findById(id).isPresent()) {
            throw new StationIllegalArgumentException("해당 지하철 역이 존재하지 않습니다");
        }
    }

    private void validateIsExistLineById(Long id) {
        if(!lineDao.findById(id).isPresent()) {
            throw new LineIllegalArgumentException("해당 노선이 존재하지 않습니다");
        }
    }
}
