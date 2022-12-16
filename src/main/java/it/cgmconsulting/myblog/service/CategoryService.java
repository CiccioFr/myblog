package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Category;
import it.cgmconsulting.myblog.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    public Optional<Category> findById(String category){
        return categoryRepository.findById(category);
    }

    public void save(Category cat){
        categoryRepository.save(cat);
    }
}
