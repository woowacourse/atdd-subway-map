package wooteco.subway.line;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    public LineResponse createLine(LineRequest lineRequest) {
        Line newLine = LineDao.save(lineRequest.getName(), lineRequest.getColor());
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    public List<LineResponse> showLines() {
        List<Line> lines = LineDao.findAll();
        return lines.stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse showLine(Long lineId) {
        Line foundLine = LineDao.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("Id에 해당하는 노선이 없습니다."));
        return new LineResponse(foundLine.getId(), foundLine.getName(), foundLine.getName());
    }
}
