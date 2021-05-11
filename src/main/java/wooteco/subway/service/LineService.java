package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.line.Line;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.service.dto.LineDto;
import wooteco.subway.service.dto.SectionDto;

import java.util.List;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    public Line createLine(LineDto lineDto, SectionDto sectionDto) {
        Line line = Line.builder()
                .name(lineDto.getName())
                .color(lineDto.getColor())
                .build();
        long id = lineRepository.save(line);
        sectionService.createSection(sectionDto, id);
        return lineRepository.findById(id);
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    public Line findById(long id) {
        return lineRepository.findById(id);
    }

    public void editLine(long id, LineDto lineDto) {
        Line line = Line.builder()
                .id(id)
                .name(lineDto.getName())
                .color(lineDto.getColor())
                .build();
        lineRepository.update(line);
    }

    public void deleteLine(long id) {
        lineRepository.deleteById(id);
        sectionService.deleteAllByLineId(id);
    }
}
