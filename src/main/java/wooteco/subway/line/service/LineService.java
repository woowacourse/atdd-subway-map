package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.repository.LineRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse save(LineRequest lineRequest) {
        validateLineName(lineRequest);
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineRepository.save(line);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    private void validateLineName(LineRequest lineRequest) {
        if (checkNameDuplicate(lineRequest)) {
            throw new IllegalArgumentException("중복된 이름의 노선이 존재합니다.");
        }
    }

    private boolean checkNameDuplicate(LineRequest lineRequest) {
        return lineRepository.findAll().stream()
                .anyMatch(line -> line.isSameName(lineRequest.getName()));
    }

    public LineResponse findLine(Long id) {
        Line newLine = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        validatesRequest(id, lineRequest);

        Line updatedLine = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineRepository.updateById(id, updatedLine);
    }

    private void validatesRequest(Long id, LineRequest lineRequest) {
        Line currentLine = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));

        String oldName = currentLine.getName();
        String newName = lineRequest.getName();

        if (validatesName(oldName, newName)) {
            throw new IllegalArgumentException("변경할 수 없는 이름입니다.");
        }
    }

    private boolean validatesName(String oldName, String newName) {
        return lineRepository.findAll().stream()
                .filter(line -> !line.isSameName(oldName))
                .anyMatch(line -> line.isSameName(newName));
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }
}
