package wooteco.subway.admin.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.repository.LineRepository;

@Service
public class LineService {
	private final LineRepository lineRepository;

	public LineService(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	public List<LineResponse> findAll() {
		return LineResponse.listOf(lineRepository.findAll());
	}

	public LineResponse findById(Long id) {
		return LineResponse.of(lineRepository.findById(id)
				.orElseThrow(NoSuchElementException::new));
	}

	@Transactional
	public LineResponse create(LineRequest request) {
		validateDuplicatedName(request);
		Line line = request.toLine();
		Line created = lineRepository.save(line);
		return LineResponse.of(created);
	}

	private void validateDuplicatedName(LineRequest request) {
		if (lineRepository.findByName(request.getName()).isPresent()) {
			throw new IllegalArgumentException("해당 라인이 이미 존재합니다.");
		}
	}

	@Transactional
	public LineResponse update(Long id, LineRequest request) {
		Line target = lineRepository.findById(id)
				.orElseThrow(NoSuchElementException::new);
		target.update(request.toLine());
		Line updated = lineRepository.save(target);
		return LineResponse.of(updated);
	}

	@Transactional
	public void delete(Long id) {
		lineRepository.deleteById(id);
	}
}
