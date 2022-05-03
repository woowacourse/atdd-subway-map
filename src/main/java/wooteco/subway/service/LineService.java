package wooteco.subway.service;


import java.util.List;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

public class LineService {

    public void save(String lineName, String color) {
        List<Line> lines = LineDao.findAll();
        for (Line line : lines) {
            if (line.isSameName(lineName)) {
                throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
            }
            if (line.isSameColor(color)) {
                throw new IllegalArgumentException("이미 존재하는 노선 색깔입니다.");
            }
        }

        LineDao.save(new Line(lineName, color));
    }

    public List<Line> findAll() {
        return LineDao.findAll();
    }

    public Line findById(Long lineId) {
        return LineDao.findById(lineId);
    }

    public void delete(Long lineId) {
        LineDao.delete(lineId);
    }

    public void update(Long lineId, String lineName, String color) {
        LineDao.update(lineId, lineName, color);
    }
}
