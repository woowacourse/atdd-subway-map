package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineInfo;
import wooteco.subway.domain.line.Lines;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.request.CreateLineRequest;
import wooteco.subway.dto.request.UpdateLineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.exception.ExceptionType;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.repository.StationRepository;
import wooteco.subway.repository.SubwayRepository;

@Service
public class LineService {

    private static final String DUPLICATE_LINE_NAME_EXCEPTION_MESSAGE = "중복되는 이름의 지하철 노선이 존재합니다.";

    private final SubwayRepository subwayRepository;
    private final StationRepository stationRepository;

    public LineService(SubwayRepository subwayRepository, StationRepository stationRepository) {
        this.subwayRepository = subwayRepository;
        this.stationRepository = stationRepository;
    }

    public List<LineResponse> findAll() {
        return Lines.of(subwayRepository.findAllLines(), subwayRepository.findAllSections())
                .toSortedList()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toUnmodifiableList());
    }

    public LineResponse find(Long id) {
        LineInfo lineInfo = subwayRepository.findExistingLine(id);
        Sections sections = new Sections(subwayRepository.findAllSectionsByLineId(id));
        return LineResponse.of(new Line(lineInfo, sections));
    }

    @Transactional
    public LineResponse save(CreateLineRequest lineRequest) {
        validateUniqueLineName(lineRequest.getName());
        Station upStation = stationRepository.findExistingStation(lineRequest.getUpStationId());
        Station downStation = stationRepository.findExistingStation(lineRequest.getDownStationId());
        Section newSection = new Section(upStation, downStation, lineRequest.getDistance());

        LineInfo newLine = new LineInfo(lineRequest.getName(), lineRequest.getColor());
        return LineResponse.of(subwayRepository.saveLine(newLine, newSection));
    }

    @Transactional
    public void update(Long id, UpdateLineRequest lineRequest) {
        String name = lineRequest.getName();
        validateExistingLine(id);
        validateUniqueLineName(name);
        subwayRepository.updateLine(new LineInfo(id, name, lineRequest.getColor()));
    }

    @Transactional
    public void delete(Long id) {
        LineInfo line = subwayRepository.findExistingLine(id);
        subwayRepository.deleteLine(line);
    }

    private void validateExistingLine(Long id) {
        boolean isExistingLine = subwayRepository.checkExistingLine(id);
        if (!isExistingLine) {
            throw new NotFoundException(ExceptionType.LINE_NOT_FOUND);
        }
    }

    private void validateUniqueLineName(String name) {
        boolean isDuplicateName = subwayRepository.checkExistingLineName(name);
        if (isDuplicateName) {
            throw new IllegalArgumentException(DUPLICATE_LINE_NAME_EXCEPTION_MESSAGE);
        }
    }
}
