package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDaoImpl;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

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

    public List<LineResponse> findAll() {
        List<Line> lines = lineDaoImpl.findAll();
        return lines.stream()
            .map(LineResponse::new)
            .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDaoImpl.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다."));
        return new LineResponse(line);
    }

    public void update(Long id, LineRequest lineRequest) {
        lineDaoImpl.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void delete(Long id) {
        lineDaoImpl.deleteById(id);
    }
}
