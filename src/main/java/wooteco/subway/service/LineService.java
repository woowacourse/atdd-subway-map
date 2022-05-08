package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.line.LineRequestDTO;
import wooteco.subway.service.dto.line.LineResponseDTO;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional(readOnly = true)
    public LineResponseDTO findById(Long id) {
        validateNonFoundId(id);

        return new LineResponseDTO(lineDao.findById(id));
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
                .map(LineResponseDTO::new)
                .collect(Collectors.toList());
    }

    public LineResponseDTO create(LineRequestDTO lineRequestDTO) {
        validateDuplicate(lineRequestDTO);
        Line line = lineDao.save(new Line(lineRequestDTO.getName(), lineRequestDTO.getColor()));

        return new LineResponseDTO(line);
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
