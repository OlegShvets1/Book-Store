package mate.academy.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

    @Sql(scripts = {"classpath:database/books/remove-books-and-category-from-db.sql",
        "classpath:database/books/remove-categories-from-db.sql",
        "classpath:database/books/add-book-and-category-to-db.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
    @Sql(scripts = "classpath:database/books/remove-books-and-category-from-db.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
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
    @Sql(scripts = "classpath:database/books/delete-added-book-from-db.sql",
              executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Create a new book")
    void  createBook_ValidRequestDto_Ok() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setAuthor("Daniel Defoe");
        requestDto.setTitle("Robinson Crusoe");
        requestDto.setPrice(DEFAULT_PRICE);
        requestDto.setCategoryIds(Set.of(1L));
        requestDto.setIsbn("121-000-001");

        BookDto expected = new BookDto();
        expected.setAuthor(requestDto.getAuthor());
        expected.setTitle(requestDto.getTitle());
        expected.setPrice(requestDto.getPrice());
        expected.setCategoryIds(requestDto.getCategoryIds());
        expected.setIsbn(requestDto.getIsbn());
        expected.setId(2L);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(post("/api/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), BookDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected, actual);
    }

//    @Test
//    @WithMockUser(username = "user", roles = {"USER"})
//    @DisplayName("")
//    void getAll_Ok() throws Exception {
//        List<BookDto> expected = List.of(getBookDto());
//
//        MvcResult result = mockMvc.perform(get("/api/books"))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        BookDto[] actual = objectMapper
//                .readValue(result.getResponse().getContentAsString(), BookDto[].class);
//        Assertions.assertNotNull(actual);
//
//        EqualsBuilder.reflectionEquals(
//                expected,
//                actual,
//                "id", "description", "coverImage");
//    }
//
//    private static BookDto getBookDto() {
//        return new BookDto()
//                .setId(1L)
//                .setTitle("Title")
//                .setAuthor("Author")
//                .setIsbn("1234")
//                .setPrice(BigDecimal.valueOf(20.00));
//    }
}