package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.repository.LineRepository;
import wooteco.subway.domain.repository.SectionRepository;
import wooteco.subway.service.dto.LineRequest;
import wooteco.subway.service.dto.LineResponse;
import wooteco.subway.utils.exception.NameDuplicatedException;
import wooteco.subway.utils.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class LineService {

    private final LineRepository lineRepository;
    private final SectionService sectionService;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, SectionService sectionService, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse create(final LineRequest lineRequest) {
        validateDuplicatedName(lineRequest);

        Line line = Line.create(lineRequest.getName(), lineRequest.getColor());
        Line savedLine = lineRepository.save(line);

        Section savedSection = sectionService.create(line.getId(), lineRequest);
        return new LineResponse(savedLine, List.of(savedSection.getUpStation(), savedSection.getDownStation()));
    }

    private void validateDuplicatedName(LineRequest lineRequest) {
        if (lineRepository.existByName(lineRequest.getName())) {
            throw new NameDuplicatedException("[ERROR] 이미 존재하는 노선의 이름입니다.");
        }
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> {
                    List<Section> sections = sectionRepository.findAllByLineId(line.getId());
                    List<Station> stations = new ArrayList<>();
                    for (Section section : sections) {
                        stations.add(section.getUpStation());
                        stations.add(section.getDownStation());
                    }
                    List<Station> collect = stations.stream().distinct().collect(Collectors.toList());
                    return new LineResponse(line, collect);
                })
                .collect(Collectors.toList());
    }

    public LineResponse showLine(final Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[ERROR] 해당하는 식별자의 노선을 찾을수 없습니다."));

        return new LineResponse(line);
    }

    public void update(final Long id, final LineRequest lineRequest) {
        lineRepository.update(id, lineRequest.toLine());
    }

    public void delete(final Long id) {
        lineRepository.deleteById(id);
    }
}
