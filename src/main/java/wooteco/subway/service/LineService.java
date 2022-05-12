package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.LineSeries;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionSeries;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.StationSeries;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository,
        SectionRepository sectionRepository,
        StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(LineRequest lineRequest) {
        LineSeries lines = new LineSeries(lineRepository.findAllLines());
        Line savedLine = lineRepository.save(lines.create(lineRequest.getName(), lineRequest.getColor()));

        final Station upStation = stationRepository.findById(lineRequest.getUpStationId());
        final Station downStation = stationRepository.findById(lineRequest.getDownStationId());
        final Distance distance = new Distance(lineRequest.getDistance());

        final Section savedSection = sectionRepository.create(
            savedLine.getId(), new Section(upStation, downStation, distance)
        );
        return LineResponse.of(savedLine, savedSection);
    }

    public List<LineResponse> findAll() {
        return lineRepository.findAllLines()
            .stream()
            .map(line -> this.findOne(line.getId()))
            .collect(Collectors.toList());
    }

    public LineResponse findOne(Long id) {
        final StationSeries stationSeries = StationSeries.fromSectionsAsOrdered(sectionRepository.readAllSections(id));
        return LineResponse.from(lineRepository.findById(id), stationSeries.getStations());
    }

    public void update(Long id, LineRequest lineRequest) {
        lineRepository.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void delete(Long id) {
        lineRepository.delete(id);
    }
}
