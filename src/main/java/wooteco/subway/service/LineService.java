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

    public LineService(LineDaoImpl lineDaoImpl) {
        this.lineDaoImpl = lineDaoImpl;
    }

    public Line create(LineRequest lineRequest) {
        final Line line = lineRequest.toEntity();
        return lineDaoImpl.save(line);
    }

    public List<Line> findAll() {
        List<Line> lines = lineDaoImpl.findAll();
        return lines;
    }

    public Line findById(Long id) {
        final Line line = lineDaoImpl.findById(id)
            .orElseThrow(() -> new LineNotFoundException("해당 노선이 없습니다.", 1));
        return line;
    }

    public void update(Long id, LineRequest lineRequest) {
        final Line targetLine = findById(id);
        targetLine.update(lineRequest.toEntity());
        lineDaoImpl.update(targetLine);
    }

    public void delete(Long id) {
        lineDaoImpl.deleteById(id);
    }
}
