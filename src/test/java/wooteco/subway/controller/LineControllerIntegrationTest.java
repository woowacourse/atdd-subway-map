package wooteco.subway.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.ui.LineController;

@WebMvcTest(LineController.class)
public class LineControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcLineDao jdbcLineDao;

    @DisplayName("노선 생성")
    @Test
    void 노선_생성() throws Exception {

    }

}
