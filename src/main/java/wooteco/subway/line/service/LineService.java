package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;

import java.util.List;
import java.util.Optional;

@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Long save(Section lines) {
        validateLine(lines.getLine());
        Long lineId = lineDao.save(lines.getLine());
        return lineId;
    }

    public void update(Long id, Line line) {
        Line duplicateLine = lineDao.findByName(line.getName())
                .orElseThrow(() -> new IllegalArgumentException("노선이 존재하지 않습니다."));

        if (!duplicateLine.getId().equals(id)) {
            throw new IllegalArgumentException("중복된 노선입니다.");
        }

        lineDao.update(id, line);
    }

    private void validateLine(Line line) {
        Optional<Line> duplicateLine = lineDao.findByName(line.getName());
        if (duplicateLine.isPresent()) {
            throw new IllegalArgumentException("중복된 노선입니다.");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 지하철 역이 없습니다."));
    }
}
