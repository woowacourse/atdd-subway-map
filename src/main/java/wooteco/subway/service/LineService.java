package wooteco.subway.service;


import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

public class LineService {

    public LineResponse save(LineRequest lineRequest) {
        String lineName = lineRequest.getName();
        String color = lineRequest.getColor();

        validateRequest(lineName, color);

        Line newLine = LineDao.save(new Line(lineName, color));
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    private void validateRequest(String lineName, String color) {
        List<Line> lines = LineDao.findAll();
        for (Line line : lines) {
            validateName(lineName, line);
            validateColor(color, line);
        }
    }

    private void validateName(String lineName, Line line) {
        if (line.isSameName(lineName)) {
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
        }
    }

    private void validateColor(String color, Line line) {
        if (line.isSameColor(color)) {
            throw new IllegalArgumentException("이미 존재하는 노선 색깔입니다.");
        }
    }

    public List<LineResponse> findAll() {
        return LineDao.findAll().stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long lineId) {
        Line line = LineDao.findById(lineId);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public void delete(Long lineId) {
        if (LineDao.delete(lineId)) {
            return;
        }
        throw new IllegalArgumentException("존재하지 않는 지하철 노선입니다.");
    }

    public void update(Long lineId, LineRequest lineRequest) {
        LineDao.update(lineId, lineRequest.getName(), lineRequest.getColor());
    }
}
