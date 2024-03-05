package mate.academy.bookstore.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import mate.academy.bookstore.dto.shoppingcart.cartitem.CartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.cartitem.CartItemUpdateDataRequestDto;
import mate.academy.bookstore.model.Role;
import mate.academy.bookstore.model.RoleName;
import mate.academy.bookstore.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Sql(scripts = "classpath:database/books/delete-books-and-shoppingCart-from-db.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/books/add-user-to-db.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/books/add-books-to-shoppingCart-to-db.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/books/delete-books-and-shoppingCart-from-db.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = "classpath:database/books/delete-user-from-db.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
public class ShoppingCartControllerTest {
    private static final Long VALID_USER_ID = 1L;
    private static final int AMOUNT_OF_BOOKS = 2;
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

    @Test
    void getShoppingCart_validUser_ok() throws Exception {
        Authentication authentication = userAuthentication(VALID_USER_ID);

        mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.cartItems.length()").value(AMOUNT_OF_BOOKS));
    }

    @Test
    void addBookToShoppingCart_validBook_ok() throws Exception {
        Authentication authentication = userAuthentication(VALID_USER_ID);
        CartItemRequestDto expected = new CartItemRequestDto();
        expected.setBookId(1L);
        expected.setQuantity(AMOUNT_OF_BOOKS);

        String jsonRequest = objectMapper.writeValueAsString(expected);

        mockMvc.perform(post("/api/cart")
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(authentication(authentication)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.bookId").value(expected.getBookId()))
                .andExpect(jsonPath("$.quantity").value(expected.getQuantity()));
    }

    @Test
    void updateBookQuantityInShoppingCart_validRequest_ok() throws Exception {
        Authentication authentication = userAuthentication(VALID_USER_ID);
        CartItemUpdateDataRequestDto expected = new CartItemUpdateDataRequestDto();
        expected.setQuantity(3);

        String jsonRequest = objectMapper.writeValueAsString(expected);

        mockMvc.perform(put("/api/cart/cart-items/1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.quantity").value(expected.getQuantity()));
    }

    @Test
    void updateBookQuantityInShoppingCart_invalidRequest_notOk() throws Exception {
        Authentication authentication = userAuthentication(VALID_USER_ID);
        CartItemUpdateDataRequestDto expected = new CartItemUpdateDataRequestDto();
        expected.setQuantity(-3);

        String jsonRequest = objectMapper.writeValueAsString(expected);

        mockMvc.perform(put("/api/cart/cart-items/1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authentication)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteBookFromShoppingCart_validCartItemId_ok() throws Exception {
        Authentication authentication = userAuthentication(VALID_USER_ID);

        mockMvc.perform(delete("/api/cart/cart-items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(authentication)))
                .andExpect(status().is2xxSuccessful());
    }

    private Authentication userAuthentication(Long userId) {
        Role adminRole = new Role(RoleName.ADMIN);
        User user = new User();
        user.setId(userId);
        user.setRoles(Set.of(adminRole));
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
}
