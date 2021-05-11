package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.line.NotFoundLineException;
import wooteco.subway.service.dto.CreateLineDto;
import wooteco.subway.service.dto.LineServiceDto;
import wooteco.subway.service.dto.SectionServiceDto;

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
        Line saveLine = lineDao.create(line);
        SectionServiceDto sectionServiceDto = SectionServiceDto.of(saveLine, createLineDto);
        sectionService.saveByLineCreate(sectionServiceDto);
        return LineServiceDto.from(saveLine);
    }

    public List<LineServiceDto> findAll() {
        return lineDao.showAll()
            .stream()
            .map(LineServiceDto::from)
            .collect(Collectors.toList());
    }

    public LineServiceDto findOne(@Valid final LineServiceDto lineServiceDto) {
        Line line = lineDao.show(lineServiceDto.getId());
        return LineServiceDto.from(line);
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
}
