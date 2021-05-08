package wooteco.subway.service.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.request.LineRequest;
import wooteco.subway.controller.dto.response.LineResponse;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.domain.line.Line;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        Line newLine = lineDao.save(lineRequest.toDomain());
        return LineResponse.of(newLine);
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineDao.findAll();
        return lines
            .stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse showLine(Long id) {
        Line line = lineDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 노선입니다."));
        return LineResponse.of(line);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line updateLine = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(updateLine);
    }

    public void deleteLine(Long id) {
        lineDao.deleteById(id);
    }
}
