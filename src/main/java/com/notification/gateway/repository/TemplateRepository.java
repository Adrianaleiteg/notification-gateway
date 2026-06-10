package com.notification.gateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.notification.gateway.model.Template;
import com.notification.gateway.model.enums.GroupArea;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    List<Template> findByGroupName(GroupArea groupName);

    @Query("SELECT t FROM Template t WHERE t.groupName = :groupName OR t.isPublic = true")
    List<Template> findByGroupNameOrPublic(GroupArea groupName);
}