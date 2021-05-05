package wooteco.subway.line;

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
}
