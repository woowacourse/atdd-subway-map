package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private static final String DUPLICATE_LINE_NAME = "지하철 노선 이름이 중복될 수 없습니다.";

    private final LineDao dao;

    public LineService(LineDao dao) {
        this.dao = dao;
    }

    public LineResponse save(LineRequest request) {
        String name = request.getName();
        checkDuplicateName(dao.isExistName(name));

        Line line = dao.save(name, request.getColor());
        return new LineResponse(line);
    }

    public List<LineResponse> findAll() {
        List<Line> lines = dao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return new LineResponse(dao.findById(id));
    }

    public void deleteById(Long id) {
        dao.delete(id);
    }

    public void update(Long id, LineRequest request) {
        String name = request.getName();
        checkDuplicateName(dao.isExistNameWithoutItself(id, name));

        dao.update(id, name, request.getColor());
    }

    private void checkDuplicateName(Boolean result) {
        if (result) {
            throw new IllegalArgumentException(DUPLICATE_LINE_NAME);
        }
    }
}
