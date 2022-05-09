package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineWithStationRequest;
import wooteco.subway.dto.LineWithStationResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineWithStationResponse save(final LineWithStationRequest lineRequest) {
        final Line line = new Line(lineRequest.getName(),
                lineRequest.getColor());
        final Line newLine = lineDao.save(line);
        return new LineWithStationResponse(
                newLine.getId(),
                newLine.getName(),
                newLine.getColor(),
                Collections.emptyList());
    }

    public List<LineWithStationResponse> findAll() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(it -> new LineWithStationResponse(it.getId(), it.getName(), it.getColor(), Collections.emptyList()))
                .collect(Collectors.toList());
    }
    public LineWithStationResponse findById(final Long id) {
        final Line line = lineDao.findById(id);
        return new LineWithStationResponse(line.getId(), line.getName(), line.getColor(), Collections.emptyList());
    }

    public void update(final Long id, final LineWithStationRequest lineRequest) {
        lineDao.update(id, new Line(lineRequest.getName(), lineRequest.getColor()));
    }

    public void deleteById(final Long id) {
        lineDao.deleteById(id);
    }
}
