package wooteco.subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.NotFoundIdException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
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
                .orElseThrow(NotFoundIdException::new);
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
            throw new DuplicateNameException();
        }
    }

    private void validateExistById(Long id) {
        boolean isExisting = lineDao.findById(id).isPresent();

        if (!isExisting) {
            throw new NotFoundIdException();
        }
    }
}
