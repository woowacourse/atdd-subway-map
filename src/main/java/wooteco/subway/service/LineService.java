package wooteco.subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;

    @Autowired
    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse createLine(LineRequest line) {
        Line newLine = Line.from(line);
        validateDuplicateName(newLine);
        return LineResponse.from(lineDao.save(newLine));
    }

    public List<LineResponse> getAllLines() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse getLineById(Long id) {
        return lineDao.findById(id)
                .map(LineResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선 ID입니다."));
    }

    public void update(Long id, LineRequest line) {
        Line newLine = Line.from(line);
        validateExistById(id);
        lineDao.update(id, newLine);
    }

    public void delete(Long id) {
        validateExistById(id);
        lineDao.deleteById(id);
    }

    private void validateDuplicateName(Line line) {
        boolean isExisting = lineDao.findByName(line.getName()).isPresent();

        if (isExisting) {
            throw new IllegalArgumentException("이미 존재하는 노선입니다.");
        }
    }

    private void validateExistById(Long id) {
        boolean isExisting = lineDao.findById(id).isPresent();

        if (!isExisting) {
            throw new IllegalArgumentException("대상 노선 ID가 존재하지 않습니다.");
        }
    }
}
