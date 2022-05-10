package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.repository.LineRepository;
import wooteco.subway.domain.repository.SectionRepository;
import wooteco.subway.service.dto.LineRequest;
import wooteco.subway.service.dto.LineResponse;
import wooteco.subway.utils.exception.DuplicatedException;
import wooteco.subway.utils.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class LineService {

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
    }

    public Line create(final LineRequest lineRequest) {
        validateDuplicatedName(lineRequest);
        Line line = Line.create(lineRequest.getName(), lineRequest.getColor());
        return lineRepository.save(line);
    }

    private void validateDuplicatedName(LineRequest lineRequest) {
        if (lineRepository.existByName(lineRequest.getName())) {
            throw new DuplicatedException("[ERROR] 이미 존재하는 노선의 이름입니다.");
        }
    }

    public List<LineResponse> getLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> {
                    Sections sections = new Sections(sectionRepository.findAllByLineId(line.getId()));
                    return new LineResponse(line, sections.getStations());
                })
                .collect(Collectors.toList());
    }

    public LineResponse getLine(final Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[ERROR] 해당하는 식별자의 노선을 찾을수 없습니다."));
        Sections sections = new Sections(sectionRepository.findAllByLineId(line.getId()));

        return new LineResponse(line, sections.getStations());
    }

    public void update(final Long id, final LineRequest lineRequest) {
        lineRepository.update(id, lineRequest.toLine());
    }

    public void delete(final Long id) {
        lineRepository.deleteById(id);
    }
}
