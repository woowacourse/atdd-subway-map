package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineRepository;
import wooteco.subway.dao.SectionRepository;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.DuplicateLineNameException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        String name = lineRequest.getName();
        validateDuplicateLineName(name);

        Line line = lineRepository.save(lineRequest.toLineDomain());

        sectionService.createSection(line.getId(), lineRequest);
        return findLineById(line.getId());
    }

    private void validateDuplicateLineName(String name) {
        lineRepository.findByName(name).ifPresent(line -> {
            throw new DuplicateLineNameException(name);
        });
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(long lineId) {
        Line line = sectionService.loadLine(lineId);
        return LineResponse.from(line);
    }

    public LineResponse updateLine(long id, String name, String color) {
        Line line = lineRepository.update(id, new Line(name, color));
        return LineResponse.from(line);
    }

    public void deleteLine(long id) {
        lineRepository.delete(id);
    }
}
