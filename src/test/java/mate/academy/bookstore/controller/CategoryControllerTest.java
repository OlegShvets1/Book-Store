package mate.academy.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.category.CategoryDto;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void applySecurity(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Sql(scripts = "classpath:database/books/delete-categories-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createCategory_validCategory_ok() throws Exception {
        CategoryDto expected = new CategoryDto("Thriller", "Thriller book");

        String jsonRequest = objectMapper.writeValueAsString(expected);

        MvcResult mvcResult = mockMvc.perform(post("/api/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), CategoryDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Sql(scripts = "classpath:database/books/add-categories-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-categories-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void findAll_returnAllCategories_ok() throws Exception {
        List<CategoryDto> expected = createFourExistingCategories();
        MvcResult mvcResult = mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CategoryDto[] actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), CategoryDto[].class);

        assertEquals(4, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Sql(scripts = "classpath:database/books/add-categories-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-categories-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void updateCategory_validCategoryId_ok() throws Exception {
        CategoryDto expected = new CategoryDto("updated History category",
                "updated History category");

        String jsonRequest = objectMapper.writeValueAsString(expected);

        MvcResult mvcResult = mockMvc.perform(put("/api/categories/2")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), CategoryDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Sql(scripts = "classpath:database/books/add-categories-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-categories-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void updateCategory_invalidCategoryId_notOk() throws Exception {
        CategoryDto expected = new CategoryDto("updated Category", "updated Category");

        String jsonRequest = objectMapper.writeValueAsString(expected);

        mockMvc.perform(post("/api/categories/99")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Sql(scripts = "classpath:database/books/add-books-and-category-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-books-and-category-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void getAllBooksByCategoryId_validCategoryId_ok() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/categories/1/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), BookDto[].class);

        assertEquals(2, actual.length);
    }

    @Sql(scripts = "classpath:database/books/add-books-and-category-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-books-and-category-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    void getAllBooksByCategoryId_invalidCategoryId_notOk() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/categories/99/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), BookDto[].class);

        assertEquals(0, actual.length);
    }

    @Sql(scripts = "classpath:database/books/add-categories-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-categories-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    void getCategoryById_validId_ok() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/categories/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CategoryDto expected = createFourExistingCategories().get(1);
        CategoryDto actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), CategoryDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    private List<CategoryDto> createFourExistingCategories() {
        CategoryDto categoryDto1 = new CategoryDto("History", "History book");
        CategoryDto categoryDto2 = new CategoryDto("Thriller", "Thriller book");
        CategoryDto categoryDto3 = new CategoryDto("Detective", "Detective book");
        CategoryDto categoryDto4 = new CategoryDto("Adventure", "Adventure book");

        List<CategoryDto> expected = new ArrayList<>();
        expected.add(categoryDto1);
        expected.add(categoryDto2);
        expected.add(categoryDto3);
        expected.add(categoryDto4);
        return expected;
    }
}
