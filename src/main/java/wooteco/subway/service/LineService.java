package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDaoImpl;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.exception.LineNotFoundException;

@Service
public class LineService {

    private final LineDaoImpl lineDaoImpl;

    public LineService(final LineDaoImpl lineDaoImpl) {
        this.lineDaoImpl = lineDaoImpl;
    }

    public Line create(final LineRequest lineRequest) {
        final Line line = lineRequest.toEntity();
        return lineDaoImpl.save(line);
    }

    public List<Line> findAll() {
        return lineDaoImpl.findAll();
    }

    public Line findById(final Long id) {
        return lineDaoImpl.findById(id)
            .orElseThrow(() -> new LineNotFoundException("해당 노선이 없습니다.", 1));
    }

    public void update(final Long id, final LineRequest lineRequest) {
        final Line targetLine = findById(id);
        targetLine.update(lineRequest.toEntity());
        lineDaoImpl.update(targetLine);
    }

    public void delete(final Long id) {
        final Line targetLine = findById(id);
        lineDaoImpl.delete(targetLine);
    }
}
