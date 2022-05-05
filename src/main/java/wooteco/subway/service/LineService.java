package wooteco.subway.service;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Service
public class LineService {

    private static final int DELETE_FAIL = 0;

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Long save(LineRequest request) {
        validateDuplicateName(request.getName());
        Line line = new Line(request.getName(), request.getColor());

        return lineDao.save(line);
    }

    private void validateDuplicateName(String name) {
        final boolean isExist = lineDao.findAll().stream()
                .anyMatch(line -> line.getName().equals(name));
        if (isExist) {
            throw new IllegalArgumentException("중복된 지하철 노선이 존재합니다.");
        }
    }

    public LineResponse findById(Long id) {
        final Line line = lineDao.findById(id);

        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll().stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(toUnmodifiableList());
    }

    public Long updateByLine(Long id, LineRequest request) {
        final Line updateLine = new Line(id, request.getName(), request.getColor());

        return lineDao.updateByLine(updateLine);
    }

    public void deleteById(Long id) {
        final int isDeleted = lineDao.deleteById(id);

        if (isDeleted == DELETE_FAIL) {
            throw new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
    }
}
