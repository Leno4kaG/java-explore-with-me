package ru.practicum.main_service.category.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main_service.category.domain.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
