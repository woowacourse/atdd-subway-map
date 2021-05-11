package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.exception.NotExistItemException;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse create(LineRequest lineRequest) {
//        createValidate(lineRequest);
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(),
            lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        Line newLine = lineDao.save(line);
        return new LineResponse(newLine);
    }

    private void createValidate(LineRequest lineRequest) {
        // 생성할 상행과 하행이 있는지 확인

        // Section 으로 분리하여 책임을 분산 -> Section 으로 어떻게 분리하지? !?! 가능하네? request에서 line으로 던져줄때 만들어서 던져주자!
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        return new LineResponse(line);
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();

        return lines.stream()
            .map(LineResponse::new)
            .collect(Collectors.toList());
    }

    public void update(Long id, LineRequest lineRequest) {
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor(),
            lineRequest.getUpStationId(), lineRequest.getDownStationId(),
            lineRequest.getDistance());

        validate(lineDao.update(line));
    }

    private void validate(int updateRow) {
        if (updateRow != 1) {
            throw new NotExistItemException();
        }
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
