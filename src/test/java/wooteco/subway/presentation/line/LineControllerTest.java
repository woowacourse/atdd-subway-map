package wooteco.subway.presentation.line;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wooteco.subway.application.line.LineService;
import wooteco.subway.domain.line.Line;
import wooteco.subway.presentation.line.dto.LineDtoAssembler;
import wooteco.subway.presentation.line.dto.LineRequest;
import wooteco.subway.presentation.line.dto.LineResponse;
import wooteco.subway.presentation.line.dto.SectionRequest;
import wooteco.subway.presentation.station.dto.StationResponse;
import wooteco.util.LineFactory;
import wooteco.util.SectionFactory;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {LineController.class})
class LineControllerTest {

    @MockBean
    private LineService lineService;

    @MockBean
    private LineDtoAssembler lineDtoAssembler;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createNewLine() throws Exception {
        createNewLine_mockInitialize();

        mockMvc.perform(post("/lines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new LineRequest("분당선", "red", 1L, 2L, 10L)
                )))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name", is("분당선")));
    }

    private void createNewLine_mockInitialize() {
        given(lineDtoAssembler.line(any(Line.class))).willReturn(
                new LineResponse(1L, "분당선", "red", Arrays.asList(
                        new StationResponse(1L, "일역"),
                        new StationResponse(2L, "이역")
                ))
        );

        given(lineService.save(any(Line.class))).willReturn(LineFactory.create(
                1L, "분당선", "red", Arrays.asList(
                        SectionFactory.create(1L, 1L, 1L, 2L, 10L),
                        SectionFactory.create(2L, 1L, 2L, 3L, 10L)
                )
        ));
    }

    @Test
    void allLines() throws Exception {
        allLine_mockInitialize();

        mockMvc.perform(get("/lines")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(1)));
    }

    private void allLine_mockInitialize() {
        given(lineService.allLines()).willReturn(
                Collections.singletonList(
                        LineFactory.create(1L, "백기선", "red", Collections.singletonList(
                                SectionFactory.create(1L, 1L, 1L, 2L, 10L)
                        ))
                )
        );

        given(lineDtoAssembler.line(any(Line.class))).willReturn(
                new LineResponse(1L, "분당선", "red", Arrays.asList(
                        new StationResponse(1L, "일역"),
                        new StationResponse(2L, "이역")
                ))
        );
    }

    @Test
    void findById() throws Exception {
        findById_mockInitialize();

        mockMvc.perform(get("/lines/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(1)));

    }

    private void findById_mockInitialize() {
        given(lineService.findById(anyLong())).willReturn(
                LineFactory.create(1L, "백기선", "red", Collections.singletonList(
                        SectionFactory.create(1L, 1L, 1L, 2L, 10L)
                ))
        );

        given(lineDtoAssembler.line(any(Line.class))).willReturn(
                new LineResponse(1L, "분당선", "red", Arrays.asList(
                        new StationResponse(1L, "일역"),
                        new StationResponse(2L, "이역")
                ))
        );
    }

    @Test
    void modifyById() throws Exception {
        mockMvc.perform(put("/lines/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new LineRequest("백기선", "red", 1L, 2L, 10L)
                        )
                )).andExpect(status().isOk());
    }


    @Test
    void deleteById() throws Exception {
        mockMvc.perform(delete("/lines/1")).
                andExpect(status().isNoContent());
    }

    @Test
    void deleteSectionByStationId() throws Exception {
        deleteSectionByStationId_mockInitialize();

        mockMvc.perform(delete("/lines/1/sections")
                .param("stationId", "1"))
                .andExpect(status().isNoContent());
    }

    private void deleteSectionByStationId_mockInitialize() {
        given(lineService.findById(anyLong())).willReturn(
                LineFactory.create(1L, "백기선", "red", Arrays.asList(
                        SectionFactory.create(1L, 1L, 1L, 2L, 10L),
                        SectionFactory.create(2L, 1L, 2L, 3L, 10L)
                ))
        );
    }

    @DisplayName("입력 검증 실패시 400")
    @Test
    void addNewSection_inFalseCase() throws Exception {
        addNewSection_mockInitialize();

        sendSectionRequest(new SectionRequest(-1L, 2L, 10L));
        sendSectionRequest(new SectionRequest(1L, -1L, 10L));
        sendSectionRequest(new SectionRequest(1L, 2L, -10L));
        sendSectionRequest(new SectionRequest(null, 2L, 10L));
        sendSectionRequest(new SectionRequest(1L, null, 10L));
        sendSectionRequest(new SectionRequest(1L, 2L, null));
        sendSectionRequest(new SectionRequest(0L, 2L, 10L));
        sendSectionRequest(new SectionRequest(1L, 0L, 10L));
        sendSectionRequest(new SectionRequest(1L, 2L, 0L));
    }

    private void addNewSection_mockInitialize() {
        given(lineDtoAssembler.line(any(Line.class))).willReturn(
                new LineResponse(1L, "분당선", "red", Arrays.asList(
                        new StationResponse(1L, "일역"),
                        new StationResponse(2L, "이역")
                ))
        );

        given(lineService.findById(anyLong())).willReturn(LineFactory.create(
                1L, "분당선", "red", Arrays.asList(
                        SectionFactory.create(1L, 1L, 1L, 2L, 10L),
                        SectionFactory.create(2L, 1L, 2L, 3L, 10L)
                )
        ));
    }

    private void sendSectionRequest(SectionRequest sectionRequest) throws Exception {
        mockMvc.perform(post("/lines/1/sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sectionRequest)))
                .andExpect(status().isBadRequest());
    }

}