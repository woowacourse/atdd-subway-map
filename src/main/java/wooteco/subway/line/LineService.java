package wooteco.subway.line;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.line.exception.ErrorCode;
import wooteco.subway.line.exception.LineException;

import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        try {
            return lineDao.findById(id)
                          .orElseThrow(() -> new LineException(ErrorCode.NOT_EXIST_LINE_ID));
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new LineException(ErrorCode.INCORRECT_SIZE_LINE_FIND_BY_ID);
        }
    }

    public Line createLine(String name, String color) {
        if (isStationExist(name)) {
            throw new LineException(ErrorCode.ALREADY_EXIST_LINE_NAME);
        }
        return lineDao.save(name, color);
    }

    private boolean isStationExist(String name) {
        try {
            return lineDao.findByName(name)
                          .isPresent();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new LineException(ErrorCode.INCORRECT_SIZE_LINE_FIND_BY_NAME);
        }
    }

    public void modifyLine(Long id, String name, String color) {
        lineDao.update(id, name, color);
    }

    public void deleteLine(Long id) {
        lineDao.delete(id);
    }
}
