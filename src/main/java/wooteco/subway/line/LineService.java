package wooteco.subway.line;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse createLine(final LineRequest lineRequest) {
        final String name = lineRequest.getName();
        final String color = lineRequest.getColor();

        validateDuplicatedLineName(name);
        final Line line = lineDao.save(new Line(name, color));
        return LineResponse.from(line);
    }

    private void validateDuplicatedLineName(final String name) {
        lineDao.findByName(name)
            .ifPresent(station -> {
                throw new IllegalStateException("중복된 이름의 노선입니다.");
            });
    }

    public List<LineResponse> findLines() {
        return lineDao.findAll().stream().
            map(LineResponse::from).
            collect(Collectors.toList());
    }

    public LineResponse findLine(final Long id) {
        final Line line = lineDao.findById(id).orElseThrow(() -> {
            throw new NoSuchElementException("해당 Id의 노선이 없습니다.");
        });
        return LineResponse.from(line);
    }

    public void updateLine(final Long id, final LineRequest lineRequest) {
        findLine(id);
        lineDao.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void deleteLine(final Long id) {
        findLine(id);
        lineDao.deleteById(id);
    }
}
