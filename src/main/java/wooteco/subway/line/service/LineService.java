package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.LineEntity;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.exception.LineError;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.station.service.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;
    private final StationService stationService;

    public LineService(LineDao lineDao, StationService stationService) {
        this.lineDao = lineDao;
        this.stationService = stationService;
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        LineEntity lineEntity = lineDao.findById(id)
                .orElseThrow(() -> new LineException(LineError.NOT_EXIST_LINE_ID));
        return new LineResponse(lineEntity);
    }

    public LineResponse createLine(LineRequest lineRequest) {
        if (isExistingLineName(lineRequest.getName())) {
            throw new LineException(LineError.ALREADY_EXIST_LINE_NAME);
        }

        if (!stationService.isPresent(lineRequest.getDownStationId()) || !stationService.isPresent(lineRequest.getUpStationId())) {
            throw new LineException(LineError.NOT_EXIST_STATION_ON_LINE_REQUEST);
        }
        Long createdLineId = lineDao.save(lineRequest.getName(), lineRequest.getColor());
        return findById(createdLineId);
    }

    private boolean isExistingLineName(String name) {
        return lineDao.findByName(name)
                .isPresent();
    }

    public void modifyLine(Long id, LineRequest lineRequest) {
        if (notExistingLine(id)) {
            throw new LineException(LineError.NOT_EXIST_LINE_ID);
        }
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        if (notExistingLine(id)) {
            throw new LineException(LineError.NOT_EXIST_LINE_ID);
        }
        lineDao.delete(id);
    }

    public void checkLineExist(Long id) {
        if (!lineDao.findById(id)
                .isPresent()) {
            throw new LineException(LineError.NOT_EXIST_LINE_ID);
        }
    }

    private boolean notExistingLine(Long id) {
        return !lineDao.findById(id)
                .isPresent();
    }
}
