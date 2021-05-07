package wooteco.subway.line;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line createLine(LineRequest lineRequest) {
        if (isStationExist(lineRequest.getName())) {
            throw new LineExistenceException("존재하는 노선 이름입니다.");
        }
        return lineDao.save(lineRequest.getName(), lineRequest.getColor());
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new LineExistenceException("존재하지 않는 노선입니다."));
    }

    private boolean isStationExist(String name) {
        return lineDao.findByName(name)
                .isPresent();
    }

    public void modifyLine(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        lineDao.delete(id);
    }
}