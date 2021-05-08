package wooteco.subway.line.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineCreateRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LineUpdateRequest;
import wooteco.subway.station.dao.StationDao;

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

        Line line = lineCreateRequest.toLine();
        Line savedLine = lineDao.save(line);

        validateIsExistStationById(lineCreateRequest.getDownStationId());
        validateIsExistStationById(lineCreateRequest.getUpStationId());

        return LineResponse.from(savedLine);
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
        Line line = findLineById(id);
        return LineResponse.of(line, new ArrayList<>());
    }

    public void delete(Long id) {
        validateIsExistLineById(id);
        lineDao.delete(id);
    }

    public void update(Long id, LineUpdateRequest lineUpdateRequest) {
        validateIsExistLineById(id);
        validateDuplicateNameExceptMyself(id, lineUpdateRequest.getName());
        Line line = lineUpdateRequest.toLine(id);
        lineDao.update(line);
    }

    private void validateDuplicateName(String lineName) {
        if (lineDao.findByName(lineName).isPresent()) {
            throw new IllegalArgumentException("같은 이름의 노선이 있습니다;");
        }
    }

    private void validateDuplicateNameExceptMyself(Long id, String lineName) {
        Optional<Line> lineByName = lineDao.findByName(lineName);
        if (lineByName.isPresent() && !lineByName.get().equalId(id)) {
            throw new IllegalArgumentException("같은 이름의 노선이 있습니다;");
        }
    }

    private void validateIsExistStationById(Long id) {
        if (!stationDao.findById(id).isPresent()) {
            throw new IllegalArgumentException("해당 지하철역이 존재하지 않습니다");
        }
    }

    private void validateIsExistLineById(Long id) {
        if (!lineDao.findById(id).isPresent()) {
            throw new IllegalArgumentException("해당 노선이 존재하지 않습니다");
        }
    }

    private Line findLineById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 노선이 존재하지 않습니다"));
    }
}
