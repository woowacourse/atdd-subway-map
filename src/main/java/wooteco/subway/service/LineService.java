package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse create(LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        try {
            Line newLine = lineDao.save(line);
            return new LineResponse(newLine);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("이미 존재하는 노선입니다.");
        }
    }

    public List<LineResponse> showAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse show(Long id) {
        try {
            Line line = lineDao.findById(id);
            return new LineResponse(line);
        } catch (EmptyResultDataAccessException e) {
            throw new EmptyResultDataAccessException("존재하지 않는 노선입니다.", 1);
        }
    }

    public void update(Long id, LineRequest lineRequest) {
        validateExist(id);
        Line line = lineRequest.toEntity();
        lineDao.updateById(id, line);
    }

    public void delete(Long id) {
        validateExist(id);
        lineDao.deleteById(id);
    }

    private void validateExist(Long id) {
        try {
            lineDao.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EmptyResultDataAccessException("존재하지 않는 노선입니다.", 1);
        }
    }

}
