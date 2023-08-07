package com.ghilly.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghilly.model.entity.CountryDAO;
import com.ghilly.repository.CountryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
public class CountryDAORestControllerIntegrationSuccessfulTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CountryRepository repository;

    @Test
    public void createCountryStatusOk200() throws Exception {
        String jp = "Japan";
        CountryDAO japan = new CountryDAO(jp);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(japan);

        mvc.perform(MockMvcRequestBuilders
                        .post("/countries/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
        assertTrue(repository.findByName(jp).isPresent());

        repository.deleteAll();
    }

    @Test
    public void getAllCountriesStatusOk200() throws Exception {
        String rus = "Russia";
        String fr = "France";
        String de = "Deutschland";
        repository.save(new CountryDAO(rus));
        repository.save(new CountryDAO(fr));
        repository.save(new CountryDAO(de));

        mvc.perform(MockMvcRequestBuilders
                        .get("/countries/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(System.out::println)
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value(rus))
                .andExpect(jsonPath("$[1].name").value(fr))
                .andExpect(jsonPath("$[2].name").value(de));
        assertTrue(repository.findByName(rus).isPresent());
        assertTrue(repository.findByName(fr).isPresent());
        assertTrue(repository.findByName(de).isPresent());

        repository.deleteAll();
    }

    @Test
    public void getCountryStatusOk200() throws Exception {
        String cn = "China";
        repository.save(new CountryDAO(cn));
        int id = repository.findByName(cn).get().getId();

        mvc.perform(MockMvcRequestBuilders
                        .get("/countries/{countryId}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(System.out::println)
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(cn));
        assertTrue(repository.findByName(cn).isPresent());

        repository.deleteAll();
    }

    @Test
    public void updateCountryStatusOk200() throws Exception {
        CountryDAO countryDAO = new CountryDAO("USSR");
        repository.save(countryDAO);
        int id = repository.findByName("USSR").get().getId();
        String newName = "Russia";
        CountryDAO toUpdate = new CountryDAO(id, newName);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(toUpdate);

        mvc.perform(MockMvcRequestBuilders
                        .put("/countries/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(System.out::println)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(newName));
        assertTrue(repository.findByName(newName).isPresent());

        repository.deleteAll();
    }

    @Test
    public void deleteCountryStatusOk200() throws Exception {
        String gr = "Greece";
        repository.save(new CountryDAO(gr));
        int id = repository.findByName(gr).get().getId();

        mvc.perform(MockMvcRequestBuilders
                        .delete("/countries/{countryId}", id))
                .andExpect(status().isOk());
        assertFalse(repository.findByName(gr).isPresent());

        repository.deleteAll();
    }
}