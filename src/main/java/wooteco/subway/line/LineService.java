package wooteco.subway.line;

import org.springframework.stereotype.Service;

@Service
public class LineService {

    public LineResponse createLine(LineRequest lineRequest) {
        Line newLine = new Line(lineRequest.getName(), lineRequest.getColor());
        Long createdLineId = LineDao.save(newLine);
        return new LineResponse(createdLineId, newLine.getName(), newLine.getColor());
    }
}
