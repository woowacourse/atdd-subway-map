package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        LineEntity lineEntity = new LineEntity(lineRequest.getName(), lineRequest.getColor());
        LineEntity newLineEntity = lineDao.save(lineEntity);

        return new LineResponse(newLineEntity.getId(), newLineEntity.getName(), newLineEntity.getColor());
    }

    public List<LineResponse> showLines() {
        List<LineEntity> lineEntities = lineDao.findAll();
        return lineEntities.stream()
            .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
            .collect(Collectors.toList());
    }

    public LineResponse showLine(Long id) {
        LineEntity lineEntity = lineDao.findById(id);
        return new LineResponse(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor());
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        lineDao.updateById(id, new LineEntity(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void deleteLine(Long id) {
        lineDao.deleteById(id);
    }
}
