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
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        Long upStationId = lineRequest.getUpStationId();
        Long downStationId = lineRequest.getDownStationId();
        int distance = lineRequest.getDistance();

        validateDuplicateLineName(name);

        Line line = lineRepository.save(new Line(name, color));
        long lineId = line.getId();

        Station upStation = stationRepository.findById(upStationId);
        Station downStation = stationRepository.findById(downStationId);
        Section section = new Section(upStation, downStation, distance);

        sectionRepository.save(lineId, section);
        return findLineById(lineId);
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
        Line line = lineRepository.findById(lineId);
        List<Section> sections = sectionRepository.findAllByLineId(lineId);

        for (Section section : sections) {
            Long upStationId = sectionRepository.getUpStationIdById(section.getId());
            section.setUpStation(stationRepository.findById(upStationId));

            Long downStationId = sectionRepository.getDownStationIdById(section.getId());
            section.setDownStation(stationRepository.findById(downStationId));
        }

        line.setSectionsFrom(sections);
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
