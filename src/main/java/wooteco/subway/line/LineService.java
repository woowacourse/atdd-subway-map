package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;

public class LineService {

    public LineResponse save(LineRequest lineRequest) {
        validateLineName(lineRequest);
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = LineDao.save(line);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    private void validateLineName(LineRequest lineRequest) {
        if (checkNameDuplicate(lineRequest)) {
            throw new IllegalArgumentException("중복된 이름의 노선이 존재합니다.");
        }
    }

    private boolean checkNameDuplicate(LineRequest lineRequest) {
        return LineDao.findAll().stream()
                .anyMatch(line -> line.isSameName(lineRequest.getName()));
    }

    public LineResponse findLine(Long id) {
        Line newLine = LineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = LineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        validateLineName(lineRequest);
        Line currentLine = LineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
        Line updatedLine = new Line(lineRequest.getName(), lineRequest.getColor());
        LineDao.update(currentLine, updatedLine);
    }
}
