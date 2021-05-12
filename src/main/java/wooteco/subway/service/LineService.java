package wooteco.subway.service;

import org.springframework.beans.factory.annotation.Autowired;
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

        Line line = this.lineRepository.save(new Line(name, color));
        long lineId = line.getId();

        Station upStation = this.stationRepository.findById(upStationId);
        Station downStation = this.stationRepository.findById(downStationId);
        Section section = new Section(upStation, downStation, distance);

        this.sectionRepository.save(lineId, section);
        return this.findLineById(lineId);
    }

    private void validateDuplicateLineName(String name) {
        this.lineRepository.findByName(name).ifPresent(line -> {
            throw new DuplicateLineNameException(name);
        });
    }

    public List<LineResponse> findAllLines() {
        return this.lineRepository.findAll().stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(long lineId) {
        Line line = this.lineRepository.findById(lineId);
        List<Section> sections = this.sectionRepository.findAllByLineId(lineId);

        for (Section section : sections) {
            Long upStationId = this.sectionRepository.getUpStationIdById(section.getId());
            section.setUpStation(stationRepository.findById(upStationId));

            Long downStationId = this.sectionRepository.getDownStationIdById(section.getId());
            section.setDownStation(stationRepository.findById(downStationId));
        }

        line.setSectionsFrom(sections);
        return LineResponse.from(line);
    }

    public LineResponse updateLine(long id, String name, String color) {
        Line line = lineRepository.update(id, new Line(name, color));
        return LineResponse.from(line);
    }
}
