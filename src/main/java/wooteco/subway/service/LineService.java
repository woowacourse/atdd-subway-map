package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.repository.LineRepository;
import wooteco.subway.domain.repository.StationRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.LineRequest;
import wooteco.subway.service.dto.LineResponse;
import wooteco.subway.utils.exception.NameDuplicatedException;
import wooteco.subway.utils.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class LineService {

    public static final String NOT_FOUND_MESSAGE = "[ERROR] %d 식별자에 해당하는 역을 찾을수 없습니다.";
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse create(final LineRequest lineRequest) {
        validateDuplicatedName(lineRequest);
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, lineRequest.getUpStationId())));

        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, lineRequest.getDownStationId())));

        Section section = new Section(upStation, downStation, lineRequest.getDistance());
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), section);
        Line savedLine = lineRepository.save(line);

        return new LineResponse(savedLine);
    }

    private void validateDuplicatedName(LineRequest lineRequest) {
        if (lineRepository.existByName(lineRequest.getName())) {
            throw new NameDuplicatedException("[ERROR] 이미 존재하는 노선의 이름입니다.");
        }
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::new)
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
