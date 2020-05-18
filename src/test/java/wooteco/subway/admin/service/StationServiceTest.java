package wooteco.subway.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.exception.DuplicateStationNameException;
import wooteco.subway.admin.repository.StationRepository;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {
	@Mock
	private StationRepository stationRepository;

	private StationService stationService;
	private Station station;

	@BeforeEach
	void setUp() {
		station = new Station("당고개");
		stationService = new StationService(stationRepository);
	}

	@Test
	void createStationNewName() {
		Station newStation = new Station("상계");
		boolean isSameName = newStation.getName().equals(station.getName());
		when(stationRepository.existsByName(newStation.getName())).thenReturn(isSameName);
		when(stationRepository.save(newStation)).thenReturn(newStation);
		assertThatCode(() -> stationService.create(newStation)).doesNotThrowAnyException();
	}

	@Test
	void createStationWithExistName() {
		Station newStation = new Station("당고개");
		boolean isSameName = newStation.getName().equals(station.getName());
		when(stationRepository.existsByName(newStation.getName())).thenReturn(isSameName);
		assertThatThrownBy(() -> stationService.create(newStation)).isInstanceOf(DuplicateStationNameException.class);
	}
}