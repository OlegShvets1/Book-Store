package mate.academy.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.CategoryMapper;
import mate.academy.bookstore.model.Category;
import mate.academy.bookstore.repository.category.CategoryRepository;
import mate.academy.bookstore.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    private static final Long DEFAULT_ID = 1L;
    private Category category;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    public void setup() {
        category = new Category();
        category.setId(DEFAULT_ID);
        category.setName("History");
        category.setDescription("history book");
    }

    @Test
    void saveCategory_validCategory_ok() {
        CategoryDto categoryDto = new CategoryDto("History", "history book");

        CategoryDto actual = categoryService.save(categoryDto);
        CategoryDto expected = new CategoryDto("History", "history book");

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void updateCategory_validId_ok() {
        Category category = new Category();
        category.setName("Adventure");
        category.setDescription("Adventure");

        CategoryDto expected = new CategoryDto("Adventure", "adventure book");
        when(categoryRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toModel(any())).thenReturn(category);
        when(categoryRepository.save(any())).thenReturn(new Category());

        CategoryDto actual = categoryService.update(DEFAULT_ID, expected);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void updateCategory_invalidId_notOk() {
        Long invalidCategoryId = 111L;
        CategoryDto categoryDto = new CategoryDto("Adventure", "adventure book");
        when(categoryRepository.findById(invalidCategoryId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(invalidCategoryId, categoryDto));
    }

    @Test
    void getCategoryById_validId_ok() {
        CategoryDto expectedCategoryDto = new CategoryDto("History", "history book");
        when(categoryRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(new Category()));
        when(categoryMapper.toDto(any())).thenReturn(expectedCategoryDto);

        CategoryDto result = categoryService.getById(DEFAULT_ID);

        assertEquals(expectedCategoryDto, result);
        assertNotNull(result);
    }

    @Test
    void getCategoryById_invalidId_notOk() {
        Long invalidCategoryId = 111L;
        when(categoryRepository.findById(invalidCategoryId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(invalidCategoryId));
    }
}
