package com.notification.gateway.repository;

import java.util.List;

import com.notification.gateway.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    List<Template> findByGroupName(String groupName);
}