package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.exception.NameDuplicationException;

import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line create(final LineRequest lineRequest) {
        checkDuplication(lineRequest.getName());
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor(), new Section(lineRequest.getUpStationId(),
                lineRequest.getDownStationId(), lineRequest.getDistance()));
        return lineDao.save(line);
    }

    private void checkDuplication(final String name) {
        if (lineDao.counts(name) > 0) {
            throw new NameDuplicationException();
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(final Long id) {
        return lineDao.findById(id);
    }

    public void edit(final Long id, final LineRequest lineRequest) {
        lineDao.edit(id, lineRequest.getName(), lineRequest.getColor(),
                lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    public void deleteById(final Long id) {
        lineDao.deleteById(id);
    }
}
