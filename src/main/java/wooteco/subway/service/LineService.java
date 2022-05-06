package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.DuplicateNameException;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        validateDuplicate(lineRequest);
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    private void validateDuplicate(LineRequest lineRequest) {
        if (hasDuplicateLine(lineRequest)) {
            throw new DuplicateNameException("이미 등록된 지하철노선입니다.");
        }
    }

    private boolean hasDuplicateLine(LineRequest lineRequest) {
        return lineDao.findAll()
                .stream()
                .anyMatch(line -> line.getName().equals(lineRequest.getName()));
    }

    public List<LineResponse> findLines() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineDao.find(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public int updateLine(Long id, LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        return lineDao.update(id, line);
    }

    public int deleteLine(Long id) {
        return lineDao.delete(id);
    }
}
