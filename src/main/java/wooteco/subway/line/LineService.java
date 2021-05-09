package wooteco.subway.line;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LineResponses;
import wooteco.subway.line.exception.ErrorCode;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.station.dto.StationResponse;

import java.util.Collections;
import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public LineResponses findAll() {
        return LineResponses.of(lineDao.findAll());
    }

    public LineResponse findById(Long id) {
        try {
            Line line = lineDao.findById(id)
                                       .orElseThrow(() -> new LineException(ErrorCode.NOT_EXIST_LINE_ID));
            List<StationResponse> stationResponses = findStationsByLineId(id);
            return new LineResponse(line, stationResponses);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new LineException(ErrorCode.INCORRECT_SIZE_LINE_FIND_BY_ID);
        }
    }

    private List<StationResponse> findStationsByLineId(Long id) {
        List<Section> sections = sectionDao.findByLineId(id);
        return Collections.emptyList();
    }

    public LineResponse createLine(LineRequest lineRequest) {
        if (isLineExist(lineRequest.getName())) {
            throw new LineException(ErrorCode.ALREADY_EXIST_LINE_NAME);
        }
        Long createdLineId = lineDao.save(lineRequest.getName(), lineRequest.getColor());
        SectionRequest sectionRequest = new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        addSection(createdLineId, sectionRequest);
        return findById(createdLineId);
    }

    private boolean isLineExist(String name) {
        try {
            return lineDao.findByName(name)
                          .isPresent();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new LineException(ErrorCode.INCORRECT_SIZE_LINE_FIND_BY_NAME);
        }
    }

    public void modifyLine(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        lineDao.delete(id);
    }

    public void addSection(Long lineId, SectionRequest sr) {
        sectionDao.save(lineId, sr.getUpStationId(), sr.getDownStationId(), sr.getDistance());
    }
}
