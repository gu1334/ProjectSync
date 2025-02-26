package com.ProjectSync.ProjectSync.services;

import com.ProjectSync.ProjectSync.repositories.ProjectRepository;
import com.ProjectSync.ProjectSync.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class ProjectServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectRepository projectRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void createProjectOrUpdate() throws Exception {




    }

}