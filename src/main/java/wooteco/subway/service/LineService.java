package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.NotFoundException;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse create(final LineRequest request) {
        final Line line = new Line(request.getName(), request.getColor());
        final Line savedStation = lineDao.insert(line)
                .orElseThrow(() -> new IllegalArgumentException("중복된 이름의 노선은 저장할 수 없습니다."));
        return LineResponse.from(savedStation);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(final Long id) {
        final Line line = lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 ID에 맞는 노선을 찾지 못했습니다."));
        return LineResponse.from(line);
    }

    public void updateById(final Long id, final LineRequest request) {
        final Line line = lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 노선이 존재하지 않습니다."));
        line.updateName(request.getName());
        line.updateColor(request.getColor());
        lineDao.updateById(id, line)
                .orElseThrow(() -> new IllegalArgumentException("중복된 이름의 노선이 존재합니다."));
    }

    public void deleteById(final Long id) {
        lineDao.deleteById(id);
    }
}
