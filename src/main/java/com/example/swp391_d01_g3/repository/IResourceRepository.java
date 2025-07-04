package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IResourceRepository extends JpaRepository<Resource, Long> {

    // Tìm resource theo type
    List<Resource> findByResourceType(Resource.ResourceType resourceType);

}