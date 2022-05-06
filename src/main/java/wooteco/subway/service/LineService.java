package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.exception.NotFoundException;

@Service
public class LineService {

    private static final String LINE_NOT_FOUND = "존재하지 않는 노선입니다.";
    private static final String DUPLICATE_LINE_NAME = "지하철 노선 이름이 중복될 수 없습니다.";

    private final LineDao dao;

    public LineService(LineDao dao) {
        this.dao = dao;
    }

    public LineResponse insert(LineRequest request) {
        String name = request.getName();
        checkDuplicateName(dao.isExistName(name));

        Line line = dao.insert(name, request.getColor());
        return new LineResponse(line);
    }

    public List<LineResponse> findAll() {
        List<Line> lines = dao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toUnmodifiableList());
    }

    public LineResponse findById(Long id) {
        Line line = dao.findById(id)
                .orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND));

        return new LineResponse(line);
    }

    public void deleteById(Long id) {
        if (dao.delete(id) == 0) {
            throw new NotFoundException(LINE_NOT_FOUND);
        }
    }

    public void update(Long id, LineRequest request) {
        String name = request.getName();
        checkDuplicateName(dao.isExistName(id, name));

        LineResponse lineResponse = findById(id);
        Line line = new Line(lineResponse.getId(), request.getName(), request.getColor());
        dao.update(line);
    }

    private void checkDuplicateName(boolean result) {
        if (result) {
            throw new IllegalArgumentException(DUPLICATE_LINE_NAME);
        }
    }
}
