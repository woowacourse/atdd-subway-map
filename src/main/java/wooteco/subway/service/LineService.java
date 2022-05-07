package wooteco.subway.service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse createLine(final LineRequest lineRequest) {
        validateLineName(lineRequest.getName());
        final Line line = lineDao.save(lineRequest.toEntity());
        return LineResponse.from(line);
    }

    private void validateLineName(final String name) {
        lineDao.findByName(name)
            .ifPresent(line -> {
                throw new IllegalStateException("이미 존재하는 노선입니다.");
            });
    }

    public List<LineResponse> showLines() {
        return lineDao.findAll()
            .stream()
            .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), Collections.emptyList()))
            .collect(Collectors.toList());
    }

    public LineResponse showLine(final Long id) {
        final Line line = lineDao.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 노선 ID가 존재하지 않습니다."));
        return new LineResponse(line.getId(), line.getName(), line.getColor(), Collections.emptyList());
    }

    public void updateLine(final Long id, final LineRequest lineRequest) {
        validateExist(id);
        lineDao.update(id, new Line(lineRequest.getName(), lineRequest.getColor()));
    }

    public void deleteLine(final Long id) {
        validateExist(id);
        lineDao.deleteById(id);
    }

    private void validateExist(final Long id) {
        lineDao.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 노선 ID가 존재하지 않습니다."));
    }
}
