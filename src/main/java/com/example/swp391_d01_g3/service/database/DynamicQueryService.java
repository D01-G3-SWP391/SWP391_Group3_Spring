package com.example.swp391_d01_g3.service.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;

@Service
public class DynamicQueryService {
    @Autowired
    private EntityManager entityManager;
    
    @SuppressWarnings("unchecked")
    public List<Object[]> executeQuery(String sql) {
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }
} 