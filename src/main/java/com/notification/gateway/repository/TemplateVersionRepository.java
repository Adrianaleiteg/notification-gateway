package com.notification.gateway.repository;

import com.notification.gateway.model.TemplateVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateVersionRepository extends JpaRepository<TemplateVersion, Long>{
    
}
