package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.LineRoute;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.LineCreateRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DuplicationException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.exception.SubwayIllegalArgumentException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional(readOnly = false)
    public LineResponse save(LineCreateRequest lineCreateRequest) {
        validateDuplicateName(lineCreateRequest.getName());
        validateAllStationsIsExist(lineCreateRequest);
        validateIfDownStationIsEqualToUpStation(lineCreateRequest);

        Line line = Line.of(null, lineCreateRequest.getName(), lineCreateRequest.getColor());
        Line savedLine = lineDao.save(line);

        sectionDao.save(Section.of(savedLine.getId(),
                lineCreateRequest.getUpStationId(),
                lineCreateRequest.getDownStationId(),
                lineCreateRequest.getDistance()));
        return LineResponse.from(savedLine);
    }

    private void validateDuplicateName(String lineName) {
        if (lineDao.findByName(lineName).isPresent()) {
            throw new DuplicationException("같은 이름의 노선이 있습니다;");
        }
    }

    private void validateAllStationsIsExist(LineCreateRequest lineCreateRequest) {
        stationDao.findById(lineCreateRequest.getDownStationId())
                .orElseThrow(() -> new NotFoundException("입력하신 하행역이 존재하지 않습니다."));
        stationDao.findById(lineCreateRequest.getUpStationId())
                .orElseThrow(() -> new NotFoundException("입력하신 상행역이 존재하지 않습니다."));
    }

    private void validateIfDownStationIsEqualToUpStation(LineCreateRequest lineCreateRequest) {
        if (lineCreateRequest.isSameStations()) {
            throw new SubwayIllegalArgumentException("상행과 하행 종점은 같을 수 없습니다.");
        }
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse find(Long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException("해당하는 노선이 존재하지 않습니다."));
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

    @Transactional(readOnly = false)
    public void delete(Long id) {
        lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException("삭제하려는 노선이 존재하지 않습니다"));
        lineDao.delete(id);
    }

    @Transactional(readOnly = false)
    public void update(Long id, LineUpdateRequest lineUpdateRequest) {
        lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException("수정하려는 노선이 존재하지 않습니다"));
        validateDuplicateNameExceptMyself(id, lineUpdateRequest.getName());
        Line line = Line.of(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor());
        lineDao.update(line);
    }

    private void validateDuplicateNameExceptMyself(Long id, String lineName) {
        Optional<Line> lineByName = lineDao.findByName(lineName);
        if (lineByName.isPresent() && !lineByName.get().getId().equals(id)) {
            throw new DuplicationException("같은 이름의 노선이 있습니다;");
        }
    }
}
