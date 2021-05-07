package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRoute;
import wooteco.subway.line.dto.LineCreateRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LineUpdateRequest;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        Line line = Line.of(lineCreateRequest);
        Line savedLine = lineDao.save(line);

        sectionDao.save(Section.of(savedLine.getId(), lineCreateRequest));
        return LineResponse.from(savedLine);
    }

    private void validateDuplicateName(String lineName) {
        if (lineDao.findByName(lineName).isPresent()) {
            throw new IllegalArgumentException("같은 이름의 노선이 있습니다;");
        }
    }

    private void validateAllStationsIsExist(LineCreateRequest lineCreateRequest) {
        stationDao.findById(lineCreateRequest.getDownStationId())
                .orElseThrow(() -> new IllegalArgumentException("입력하신 하행역이 존재하지 않습니다."));
        stationDao.findById(lineCreateRequest.getUpStationId())
                .orElseThrow(() -> new IllegalArgumentException("입력하신 상행역이 존재하지 않습니다."));
    }

    private void validateIfDownStationIsEqualToUpStation(LineCreateRequest lineCreateRequest) {
        if (lineCreateRequest.isSameStations()) {
            throw new IllegalArgumentException("상행과 하행 종점은 같을 수 없습니다.");
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
        lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제하려는 노선이 존재하지 않습니다"));
        lineDao.delete(id);
    }

    public void update(Long id, LineUpdateRequest lineUpdateRequest) {
        lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정하려는 노선이 존재하지 않습니다"));
        validateDuplicateNameExceptMyself(id, lineUpdateRequest.getName());
        Line line = Line.of(id, lineUpdateRequest);
        lineDao.update(line);
    }

    private void validateDuplicateNameExceptMyself(Long id, String lineName) {
        Optional<Line> lineByName = lineDao.findByName(lineName);
        if (lineByName.isPresent() && !lineByName.get().getId().equals(id)) {
            throw new IllegalArgumentException("같은 이름의 노선이 있습니다;");
        }
    }
}
