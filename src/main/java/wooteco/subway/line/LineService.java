package wooteco.subway.line;

public class LineService {

    public LineResponse save(LineRequest lineRequest) {
        validateStationName(lineRequest);
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = LineDao.save(line);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    private void validateStationName(LineRequest lineRequest) {
        if (checkNameDuplicate(lineRequest)) {
            throw new IllegalArgumentException("중복된 이름의 노선이 존재합니다.");
        }
    }

    private boolean checkNameDuplicate(LineRequest lineRequest) {
        return LineDao.findAll().stream()
                .anyMatch(line -> line.isSameName(lineRequest.getName()));
    }
}
