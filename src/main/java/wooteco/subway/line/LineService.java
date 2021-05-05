package wooteco.subway.line;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

public class LineService {
    private static final LineService LINE_SERVICE = new LineService();

    private LineService() {
    }

    public static LineService getInstance() {
        return LINE_SERVICE;
    }

    public LineResponse createLine(final LineRequest lineRequest) {
        final String name = lineRequest.getName();
        final String color = lineRequest.getColor();

        validateDuplicatedLineName(name);
        final Line line = LineDao.save(new Line(name, color));
        return LineResponse.from(line);
    }

    private void validateDuplicatedLineName(final String name) {
        LineDao.findByName(name)
            .ifPresent(station -> {
                throw new IllegalStateException("중복된 이름의 노선입니다.");
            });
    }

    public List<LineResponse> findLines() {
        return LineDao.findALl().stream().
            map(LineResponse::from).
            collect(Collectors.toList());
    }

    public LineResponse findLine(final Long id) {
        final Line line = LineDao.findById(id).orElseThrow(() -> {
            throw new NoSuchElementException("해당 Id의 노선이 없습니다.");
        });
        return LineResponse.from(line);
    }
}
