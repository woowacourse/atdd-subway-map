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
            throw new LineExistenceException();
        }
        return lineDao.save(lineRequest.getName(), lineRequest.getColor());
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(LineExistenceException::new);
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