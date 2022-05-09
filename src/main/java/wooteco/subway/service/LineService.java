package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateLineException;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public LineResponse create(LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        validateUnique(line);
        Line newLine = lineDao.save(line);
        return new LineResponse(newLine);
    }

    private void validateUnique(Line line) {
        if (lineDao.existsName(line)) {
            throw new DuplicateLineException("이미 존재하는 노선 이름입니다.");
        }
        if (lineDao.existsColor(line)) {
            throw new DuplicateLineException("이미 존재하는 노선 색상입니다.");
        }
    }

    public List<LineResponse> showAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse show(Long id) {
        validateExist(id);
        Line line = lineDao.findById(id);
        return new LineResponse(line);
    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
        validateExist(id);
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        validateUnique(line);
        lineDao.updateById(id, line);
    }

    private void validateExist(Long id) {
        if (!lineDao.existsId(id)) {
            throw new DataNotFoundException("존재하지 않는 노선입니다.");
        }
    }

    public void delete(Long id) {
        validateExist(id);
        lineDao.deleteById(id);
    }

}
