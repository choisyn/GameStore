package com.gamestore.repository;

import com.gamestore.entity.CommunitySection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunitySectionRepository extends JpaRepository<CommunitySection, Long> {
    
    // 查找启用的板块，按排序顺序
    List<CommunitySection> findByStatusOrderBySortOrderAsc(CommunitySection.SectionStatus status);
    
    // 按名称查找
    CommunitySection findByName(String name);
}

