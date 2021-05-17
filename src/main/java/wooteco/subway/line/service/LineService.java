package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Long save(LineRequest lineRequest) {
        Line line = lineRequest.toLineEntity();
        validateLine(line);
        return lineDao.save(line);
    }

    public void update(Long id, LineRequest lineRequest) {
        validLineId(id);
        lineDao.findByName(lineRequest.getName())
                .filter(line -> !line.sameAs(id))
                .ifPresent(line -> {
                    throw new IllegalArgumentException("중복된 노선입니다.");
                });

        lineDao.update(id, lineRequest.toLineEntity());
    }

    private void validLineId(Long id) {
        lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("노선이 존재하지 않습니다."));
    }

    private void validateLine(Line line) {
        lineDao.findByName(line.getName())
                .ifPresent(matchedLine -> {
                    throw new IllegalArgumentException("중복된 노선입니다.");
                });
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return lineDao.findById(id)
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .orElseThrow(() -> new IllegalArgumentException("해당 지하철 역이 없습니다."));
    }
}
