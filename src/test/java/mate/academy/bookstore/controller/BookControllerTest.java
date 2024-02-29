package mate.academy.bookstore.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    private static final Long DEFAULT_ID = 1L;
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(69.0);
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void applySecurity(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void getAllBooks_Ok() throws Exception {
        List<BookDto> expected = List.of(getBookDto());

        MvcResult result = mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto[].class);
        Assertions.assertNotNull(actual);

        EqualsBuilder.reflectionEquals(expected, actual,
                "id", "description", "coverImage");
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @Sql(scripts = "classpath:database/books/add-books-and-category-to-db.sql",
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-books-and-category-from-db.sql",
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBookById_ValidId_Ok() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals("Shantaram", actual.getTitle());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Sql(scripts = "classpath:database/books/add-books-and-category-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-books-and-category-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ValidBook_ok() throws Exception {
        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto();
        bookRequestDto.setAuthor("Daniel Defoe");
        bookRequestDto.setTitle("Robinson Crusoe");
        bookRequestDto.setPrice(DEFAULT_PRICE);
        bookRequestDto.setCategoryIds(Set.of(1L));
        bookRequestDto.setIsbn("121-000-001");

        BookDto expected = getBookDto();

        String jsonRequest = objectMapper.writeValueAsString(bookRequestDto);

        MvcResult result = mockMvc.perform(post("/api/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);

        EqualsBuilder
                .reflectionEquals(expected, actual, "id", "description", "coverImage");
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Sql(scripts = "classpath:database/books/add-books-and-category-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-books-and-category-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBook_WithValidRequest_ok() throws Exception {
        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto();
        bookRequestDto.setAuthor("Daniel Defoe");
        bookRequestDto.setTitle("Robinson Crusoe");
        bookRequestDto.setPrice(BigDecimal.valueOf(75));
        bookRequestDto.setCategoryIds(Set.of(1L));
        bookRequestDto.setIsbn("121-000-001");

        BookDto expected = new BookDto();
        expected.setAuthor(bookRequestDto.getAuthor());
        expected.setTitle(bookRequestDto.getTitle());
        expected.setPrice(bookRequestDto.getPrice());
        expected.setCategoryIds(bookRequestDto.getCategoryIds());
        expected.setIsbn(bookRequestDto.getIsbn());
        expected.setId(1L);

        String jsonRequest = objectMapper.writeValueAsString(bookRequestDto);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/books/1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BookDto actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), BookDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Sql(scripts = "classpath:database/books/add-books-and-category-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-books-and-category-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBook_ValidId_Ok() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().is(204))
                .andReturn();
    }

    @Test
    public void findAllBooks_WithNotAuthorisedUser_notOk() throws Exception {
        mockMvc.perform(
                        get("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    public void getAllBooksEndpoint_ok() throws Exception {
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @Sql(scripts = "classpath:database/books/add-books-and-category-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-books-and-category-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getBooksPriceGreaterThan120_ok() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("price", "120")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.price > 120)]").exists())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @Sql(scripts = "classpath:database/books/add-books-and-category-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-books-and-category-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getBooksByAuthorAndTitle_ok() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("author", "Stephen King")
                        .param("title", "The Shining")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.author == 'Jack London' "
                        + "&& @.title == 'White Fang')]").exists())
                .andExpect(jsonPath("$[?(@.author == 'Jack London' "
                        + "&& @.title == 'White Fang')]", hasSize(1)))
                .andReturn();
    }

    private static BookDto getBookDto() {
        return new BookDto()
                .setId(DEFAULT_ID)
                .setTitle("Robinson Crusoe")
                .setAuthor("Daniel Defoe")
                .setIsbn("121-000-001")
                .setPrice(DEFAULT_PRICE);
    }
}
