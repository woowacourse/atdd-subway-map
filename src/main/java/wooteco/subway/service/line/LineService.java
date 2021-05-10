package wooteco.subway.service.line;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.controller.dto.request.line.LineCreateRequestDto;
import wooteco.subway.controller.dto.request.line.LineUpdateRequestDto;
import wooteco.subway.controller.dto.response.line.LineCreateResponseDto;
import wooteco.subway.controller.dto.response.line.LineResponseDto;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.exception.HttpException;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public LineCreateResponseDto createLine(LineCreateRequestDto lineCreateRequestDto) {
        validateSection(lineCreateRequestDto.getUpStationId(), lineCreateRequestDto.getDownStationId());
        Line savedLine = getSavedLine(lineCreateRequestDto.getName(), lineCreateRequestDto.getColor());
        Section savedSection = getSavedSection(savedLine,
            lineCreateRequestDto.getUpStationId(), lineCreateRequestDto.getDownStationId(), lineCreateRequestDto.getDistance());
        return new LineCreateResponseDto(savedLine, savedSection);
    }

    private void validateSection(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new HttpException(BAD_REQUEST, "상행 종점역과 하행 종점역은 같을 수 없습니다.");
        }
    }

    private Line getSavedLine(String name, String color) {
        try {
            Line newLine = new Line(name, color);
            return lineDao.save(newLine);
        } catch (DuplicateKeyException e) {
            throw new HttpException(BAD_REQUEST, "생성할 노선의 이름 또는 색깔이 이미 사용중입니다.");
        }
    }

    private Section getSavedSection(Line savedLine, Long upStationId, Long downStationId, int distance) {
        try {
            Section newSection = new Section(savedLine, upStationId, downStationId, distance);
            return sectionDao.save(newSection);
        } catch (DataIntegrityViolationException e) {
            throw new HttpException(BAD_REQUEST, "생성할 노선의 상행 종점역 또는 하행 종점역이 존재하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponseDto> getAllLines() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
            .map(LineResponseDto::new)
            .collect(Collectors.toList());
    }

    public int updateLine(Long id, LineUpdateRequestDto lineUpdateRequestDto) {
        return lineDao.update(id, lineUpdateRequestDto.getName(), lineUpdateRequestDto.getColor());
    }

    public int deleteLineById(Long id) {
        sectionDao.deleteAllByLineId(id);
        return lineDao.deleteById(id);
    }
}
