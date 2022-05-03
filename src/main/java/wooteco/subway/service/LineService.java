package wooteco.subway.service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import java.util.List;
import java.util.stream.Collectors;

public class LineService {

    public static LineResponse createLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = LineDao.save(line);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    public static List<LineResponse> findLines() {
        List<Line> lines = LineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public static LineResponse findLine(Long id) {
        Line line = LineDao.find(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public static void updateLine(Long id, LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        LineDao.update(id, line);
    }

    public static void deleteLine(Long id) {
        LineDao.delete(id);
    }
}
