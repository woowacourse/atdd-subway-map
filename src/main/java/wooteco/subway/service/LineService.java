package wooteco.subway.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.error.exception.NotFoundException;

@Transactional(readOnly = true)
@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        if (lineDao.existsByName(lineRequest.getName())) {
            throw new IllegalArgumentException(lineRequest.getName() + "은 이미 존재하는 노선 이름입니다.");
        }

        Line line = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        return new LineResponse(line);
    }

    public LineResponse findById(Long id) {
        Line line = getLine(id);
        return new LineResponse(line);
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(toList());
    }

    @Transactional
    public LineResponse update(Long id, LineRequest lineRequest) {
        getLine(id);

        try {
            lineDao.update(id, new Line(lineRequest.getName(), lineRequest.getColor()));
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(lineRequest.getName() + "은 이미 존재하는 노선 이름입니다.");
        }

        return new LineResponse(getLine(id));
    }

    @Transactional
    public void deleteById(Long id) {
        getLine(id);
        lineDao.deleteById(id);
    }

    private Line getLine(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException(id + " 의 노선은 존재하지 않습니다."));
    }
}
