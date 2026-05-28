package br.com.checklistweb.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> findAllActive() {
        return categoryRepository.findByActiveTrueOrderByDisplayOrderAscNameAsc()
                .stream()
                .map(CategoryResponse::new)
                .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setActive(true);

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponse(savedCategory);
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findEntityById(id);

        category.setName(request.getName());
        category.setDisplayOrder(request.getDisplayOrder());

        return new CategoryResponse(category);
    }

    @Transactional
    public void deactivate(Long id) {
        Category category = findEntityById(id);
        category.setActive(false);
    }

    public Category findEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada: " + id));
    }
}