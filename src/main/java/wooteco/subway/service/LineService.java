package wooteco.subway.service;

import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.CommonLineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.service.dto.LineDto;

@Service
public class LineService {

    private static final int NO_ROW_AFFECTED = 0;
    private static final String LINE_NOT_FOUND = "요청한 노선이 존재하지 않습니다. ";

    private final CommonLineDao lineDao;
    private final LineRepository lineRepository;

    public LineService(final CommonLineDao lineDao, final LineRepository lineRepository) {
        this.lineDao = lineDao;
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse save(final LineRequest lineRequest) {
//        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        final LineDto lineDto = new LineDto(lineRequest.getName(), lineRequest.getColor(),
                lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        final Line line = lineRepository.save(lineDto);
        return LineResponse.from(line);
//        return lineRepository.save(lineDto);
    }

    @Transactional(readOnly = true)
    public Line findById(final Long id) {
        return lineDao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Line> findAll() {
        return lineDao.findAll();
    }

    @Transactional
    public void update(final Long id, final LineRequest lineRequest) {
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        final int theNumberOfAffectedRow = lineDao.update(id, line);
        if (theNumberOfAffectedRow == NO_ROW_AFFECTED) {
            throw new IllegalStateException(LINE_NOT_FOUND + "id=" + id + " " + line);
        }
    }

    @Transactional
    public void deleteById(final Long id) {
        final int theNumberOfAffectedRow = lineDao.deleteById(id);
        if (theNumberOfAffectedRow == NO_ROW_AFFECTED) {
            throw new IllegalStateException(LINE_NOT_FOUND + "id=" + id);
        }
    }
}
