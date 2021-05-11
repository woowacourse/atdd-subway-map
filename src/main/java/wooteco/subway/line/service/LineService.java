package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.exception.InvalidLineNameException;
import wooteco.subway.line.exception.WrongLineIdException;

import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line) {
        if (isDuplicatedName(line)) {
            throw new InvalidLineNameException(String.format("노선 이름이 중복되었습니다. 중복된 노선 이름 : %s", line.getName()));
        }
        if (isDuplicatedColor(line)) {
            throw new InvalidLineNameException(String.format("노선 색상이 중복되었습니다. 중복된 노선 색상 : %s", line.getColor()));
        }
        return lineDao.save(line);
    }

    private boolean isDuplicatedName(Line line) {
        return lineDao.checkExistName(line.getName());
    }

    private boolean isDuplicatedColor(Line line) {
        return lineDao.checkExistColor(line.getColor());
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id);
    }

    public void update(Line line) {
        ifAbsent(line);
        lineDao.update(line);
    }

    public void delete(Line line) {
        ifAbsent(line);
        lineDao.delete(line);
    }

    private void ifAbsent(Line line) {
        if (!lineDao.checkExistId(line.getId())) {
            throw new WrongLineIdException("노선이 존재하지 않습니다.");
        }
    }
}
