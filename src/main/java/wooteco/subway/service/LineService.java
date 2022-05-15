package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.line.LineRequestDto;
import wooteco.subway.service.dto.line.LineResponseDto;
import wooteco.subway.service.dto.section.SectionRequestDto;
import wooteco.subway.service.dto.station.StationResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, StationService stationService, SectionService sectionService) {
        this.lineDao = lineDao;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @Transactional(readOnly = true)
    public LineResponseDto findById(Long id) {
        validateNonFoundId(id);

        return makeLineResponseDto(lineDao.findById(id));
    }

    private void validateNonFoundId(Long id) {
        if (!lineDao.existById(id)) {
            throw new NoSuchElementException("[ERROR] 해당 노선이 존재하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponseDto> findAll() {
        List<Line> lines = lineDao.findAll();

        return lines.stream()
                .map(this::makeLineResponseDto)
                .collect(Collectors.toList());
    }

    public LineResponseDto create(LineRequestDto lineRequestDto) {
        validateDuplicate(lineRequestDto);
        Line line = lineDao.create(new Line(lineRequestDto.getName(), lineRequestDto.getColor()));
        sectionService.create(new SectionRequestDto(line.getId(), lineRequestDto.getUpStationId(), lineRequestDto.getDownStationId(), lineRequestDto.getDistance()));

        return makeLineResponseDto(line);
    }

    private LineResponseDto makeLineResponseDto(Line line) {
        Sections sections = sectionService.findAllByLineId(line.getId());
        List<StationResponseDto> stations = new ArrayList<>();
        Long upStationId = sections.findFinalUpStationId();
        addStations(upStationId, sections, stations);

        return new LineResponseDto(line, stations);
    }

    private void addStations(Long upStationId, Sections sections, List<StationResponseDto> stations) {
        if (sections.hasUpStationId(upStationId)){
            Section section = sections.getSectionByUpStationId(upStationId);
            Station station = stationService.findById(upStationId);
            stations.add(new StationResponseDto(station));
            addStations(section.getDownStationId(), sections, stations);
            return;
        }
        Station station = stationService.findById(upStationId);
        stations.add(new StationResponseDto(station));
    }

    private void validateDuplicate(LineRequestDto lineRequestDto) {
        validateDuplicateName(lineRequestDto.getName());
        validateDuplicateColor(lineRequestDto.getColor());
    }

    private void validateDuplicateName(String name) {
        if (lineDao.existByName(name)) {
            throw new IllegalArgumentException("[ERROR] 중복된 이름이 존재합니다.");
        }
    }

    private void validateDuplicateColor(String color) {
        if (lineDao.existByColor(color)) {
            throw new IllegalArgumentException("[ERROR] 중복된 색이 존재합니다.");
        }
    }

    public void updateById(Long id, LineRequestDto lineRequestDto) {
        validateNonFoundId(id);
        validateExistName(id, lineRequestDto.getName());
        validateExistColor(id, lineRequestDto.getColor());

        lineDao.update(id, lineRequestDto.getName(), lineRequestDto.getColor());
    }

    public void deleteById(Long id) {
        validateNonFoundId(id);

        lineDao.deleteById(id);
    }

    private void validateExistName(Long id, String name) {
        lineDao.findAll().stream()
                .filter(it -> !it.getId().equals(id))
                .filter(it -> it.getName().equals(name))
                .findAny()
                .ifPresent(s -> {
                    throw new NoSuchElementException("[ERROR] 이미 존재하는 이름입니다.");
                });
    }

    private void validateExistColor(Long id, String color) {
        lineDao.findAll().stream()
                .filter(it -> !it.getId().equals(id))
                .filter(it -> it.getColor().equals(color))
                .findAny()
                .ifPresent(s -> {
                    throw new NoSuchElementException("[ERROR] 이미 존재하는 색상입니다.");
                });
    }
}
