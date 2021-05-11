package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.line.DuplicateLineException;
import wooteco.subway.exception.line.NotFoundLineException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.CreateLineDto;
import wooteco.subway.line.dto.LineServiceDto;
import wooteco.subway.line.dto.ReadLineDto;
import wooteco.subway.section.SectionService;
import wooteco.subway.section.dto.CreateSectionDto;
import wooteco.subway.section.dto.DeleteStationDto;
import wooteco.subway.section.dto.SectionServiceDto;
import wooteco.subway.station.dto.StationResponse;

@Service
public class LineService {

    private static final int NOT_FOUND = 0;

    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(final LineDao lineDao, final SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineServiceDto createLine(@Valid final CreateLineDto createLineDto) {
        Line line = createLineDto.toLineEntity();
        Line saveLine = lineDao.save(line);
        SectionServiceDto sectionServiceDto = SectionServiceDto.of(saveLine, createLineDto);
        sectionService.saveByLineCreate(sectionServiceDto);
        return LineServiceDto.from(saveLine);
    }

    private void checkExistedNameAndColor(CreateLineDto createLineDto) {
        String name = createLineDto.getName();
        String color = createLineDto.getColor();

        if (lineDao.countByColor(color) != 0) {
            throw new DuplicateLineException();
        }

        if (lineDao.countByName(name) != 0) {
            throw new DuplicateLineException();
        }
    }

    public List<LineServiceDto> findAll() {
        return lineDao.showAll()
            .stream()
            .map(LineServiceDto::from)
            .collect(Collectors.toList());
    }

    public ReadLineDto findOne(@Valid final LineServiceDto lineServiceDto) {
        Line line = lineDao.show(lineServiceDto.getId());
        List<StationResponse> stationResponses = sectionService.findAllbyLindId(line.getId());
        return ReadLineDto.of(line, stationResponses);
    }

    public void update(@Valid final LineServiceDto lineServiceDto) {
        Line line = lineServiceDto.toEntity();

        if (lineDao.update(lineServiceDto.getId(), line) == NOT_FOUND) {
            throw new NotFoundLineException();
        }
    }

    public void delete(@Valid final LineServiceDto lineServiceDto) {
        if (lineDao.delete(lineServiceDto.getId()) == NOT_FOUND) {
            throw new NotFoundLineException();
        }
    }

    public void createSection(CreateSectionDto createSectionDto) {
        SectionServiceDto sectionServiceDto = SectionServiceDto.from(createSectionDto);
        sectionService.save(sectionServiceDto);
    }

    public void deleteStation(final long lineId, final long stationId) {
        DeleteStationDto deleteStationDto = new DeleteStationDto(lineId, stationId);
        sectionService.delete(deleteStationDto);
    }
}
