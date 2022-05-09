package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.line.LineFindResponse;
import wooteco.subway.service.dto.line.LineSaveRequest;
import wooteco.subway.service.dto.line.LineSaveResponse;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineSaveResponse save(LineSaveRequest lineSaveRequest) {
        Line line = new Line(lineSaveRequest.getName(), lineSaveRequest.getColor());
        validateDuplicationName(line);
        Long savedId = lineDao.save(line);
        return new LineSaveResponse(savedId, line.getName(), line.getColor());
    }

    private void validateDuplicationName(Line line) {
        if (lineDao.exists(line)) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }
    }

    public List<LineFindResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
            .map(i -> new LineFindResponse(i.getId(), i.getName(), i.getColor()))
            .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        return lineDao.deleteById(id);
    }

    public boolean updateById(Long id, Line line) {
        return lineDao.updateById(id, line);
    }
}
