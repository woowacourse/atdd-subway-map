package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.service.dto.line.LineRequestDTO;
import wooteco.subway.service.dto.line.LineResponseDTO;
import wooteco.subway.service.dto.station.StationResponseDto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional(readOnly = true)
    public LineResponseDTO findById(Long id) {
        validateNonFoundId(id);

        return makeLineResponseDto(lineDao.findById(id));
    }

    private void validateNonFoundId(Long id) {
        if (!lineDao.existById(id)) {
            throw new NoSuchElementException("[ERROR] 해당 노선이 존재하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponseDTO> findAll() {
        List<Line> lines = lineDao.findAll();

        return lines.stream()
                .map(this::makeLineResponseDto)
                .collect(Collectors.toList());
    }

    public LineResponseDTO create(LineRequestDTO lineRequestDTO) {
        validateDuplicate(lineRequestDTO);
        Line line = lineDao.create(new Line(lineRequestDTO.getName(), lineRequestDTO.getColor()));
        Section newSection = new Section(line.getId(), lineRequestDTO.getUpStationId(), lineRequestDTO.getDownStationId(), lineRequestDTO.getDistance());
        Sections sections = new Sections(sectionDao.findAllByLineId(newSection.getLineId()));
        sections.validateAddNewSection(newSection);
        sectionDao.create(newSection);

        return makeLineResponseDto(line);
    }

    private LineResponseDTO makeLineResponseDto(Line line) {
        Sections sections = new Sections(sectionDao.findAllByLineId(line.getId()));
        List<Long> stationIds = sections.getStationIds();
        List<StationResponseDto> stations = stationDao.findAll().stream()
                .filter(it -> stationIds.contains(it.getId()))
                .map(StationResponseDto::new)
                .collect(Collectors.toList());

        return new LineResponseDTO(line, stations);
    }

    private void validateDuplicate(LineRequestDTO lineRequestDTO) {
        validateDuplicateName(lineRequestDTO.getName());
        validateDuplicateColor(lineRequestDTO.getColor());
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

    public void updateById(Long id, LineRequestDTO lineRequestDTO) {
        validateNonFoundId(id);
        validateExistName(id, lineRequestDTO.getName());
        validateExistColor(id, lineRequestDTO.getColor());

        lineDao.update(id, lineRequestDTO.getName(), lineRequestDTO.getColor());
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
