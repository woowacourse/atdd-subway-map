package wooteco.subway.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.exception.ExistingNameException;
import wooteco.subway.admin.exception.NotFoundException;
import wooteco.subway.admin.repository.LineRepository;

@Service
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public List<LineResponse> findAllWithoutStations() {
        return lineRepository.findAll().stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    public LineResponse findLineWithoutStations(Long id) {
        Line line = findById(id);
        return LineResponse.from(line);
    }

    public Long save(Line line) {
        validateDuplicateName(line);
        return lineRepository.save(line).getId();
    }

    public void updateLine(Long id, Line line) {
        Line persistLine = findById(id);
        persistLine.update(line);
        lineRepository.save(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private void validateDuplicateName(Line lineToCreate) {
        boolean exist = lineRepository.existsLineBy(lineToCreate.getName().trim());
        if (exist) {
            throw new ExistingNameException(lineToCreate.getName());
        }
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(id));
    }
}
