package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;

public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse createLine(String name, String color) {
        Line line = new Line(name, color);
        Line save = this.lineRepository.save(line);
        return new LineResponse(save.getId(), save.getName(), save.getColor());
    }

    public List<LineResponse> findAll() {
        return lineRepository.findAll().stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
    }
}
