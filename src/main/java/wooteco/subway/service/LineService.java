package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.controller.dto.response.StationResponse;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Lines;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.service.dto.CreateLineDto;
import wooteco.subway.service.dto.CreateSectionDto;
import wooteco.subway.service.dto.DeleteStationDto;
import wooteco.subway.service.dto.LineServiceDto;
import wooteco.subway.service.dto.ReadLineDto;
import wooteco.subway.service.dto.SectionServiceDto;

@Service
public class LineService {

    private static final int NOT_FOUND = 0;

    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineServiceDto createLine(@Valid CreateLineDto createLineDto) {
        Line line = createLineDto.toLineEntity();
        Line saveLine = lineDao.create(line);
        SectionServiceDto sectionServiceDto = SectionServiceDto.of(saveLine, createLineDto);
        sectionService.saveByLineCreate(sectionServiceDto);
        return LineServiceDto.from(saveLine);
    }

    @Transactional
    public List<LineServiceDto> findAll() {
        return lineDao.showAll()
            .stream()
            .map(LineServiceDto::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public ReadLineDto findOne(@Valid LineServiceDto lineServiceDto) {
        Line line = lineDao.show(lineServiceDto.getId());
        List<StationResponse> stationResponses = sectionService.findAllbyLindId(line.getId());
        return ReadLineDto.of(line, stationResponses);
    }

    @Transactional
    public void update(@Valid LineServiceDto lineServiceDto) {
        Line line = lineServiceDto.toEntity();

        if (lineDao.update(lineServiceDto.getId(), line) == NOT_FOUND) {
            throw new NotFoundException();
        }
    }

    @Transactional
    public void delete(@Valid LineServiceDto lineServiceDto) {
        if (lineDao.delete(lineServiceDto.getId()) == NOT_FOUND) {
            throw new NotFoundException();
        }
    }

    @Transactional
    public void createSection(@Valid CreateSectionDto createSectionDto) {
        SectionServiceDto sectionServiceDto = SectionServiceDto.from(createSectionDto);
        sectionService.save(sectionServiceDto);
    }

    @Transactional
    public void deleteStation(@NotNull Long lineId, @NotNull Long stationId) {
        DeleteStationDto deleteStationDto = new DeleteStationDto(lineId, stationId);
        sectionService.delete(deleteStationDto);
    }
}
