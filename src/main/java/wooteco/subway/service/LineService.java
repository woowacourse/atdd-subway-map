package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

import java.util.List;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line) {
        checkDuplication(line);
        return lineDao.save(line);
    }

    private void checkDuplication(Line line) {
        if(lineDao.getLinesHavingName(line.getName()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id);
    }

    public void edit(Long id, String name, String color) {
        lineDao.edit(id, name, color);
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
