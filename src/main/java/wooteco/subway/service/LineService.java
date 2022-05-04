package wooteco.subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;

    @Autowired
    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line createLine(Line line) {
        validateDuplicateName(line);
        return lineDao.save(line);
    }

    public List<Line> getAllLines() {
        return lineDao.findAll();
    }

    public Line getLineById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선 ID입니다."));
    }

    public void update(Long id, Line newLine) {
        validateExist(id);
        lineDao.update(id, newLine);
    }

    public void delete(Long id) {
        validateExist(id);
        lineDao.deleteById(id);
    }

    private void validateDuplicateName(Line line) {
        List<String> names = lineDao.findAll().stream()
                .map(Line::getName)
                .collect(Collectors.toList());

        if (names.contains(line.getName())) {
            throw new IllegalArgumentException("이미 존재하는 노선입니다.");
        }
    }

    private void validateExist(Long id) {
        List<Long> lineIds = lineDao.findAll().stream()
                .map(Line::getId)
                .collect(Collectors.toList());

        if (!lineIds.contains(id)) {
            throw new IllegalArgumentException("대상 노선 ID가 존재하지 않습니다.");
        }
    }
}
