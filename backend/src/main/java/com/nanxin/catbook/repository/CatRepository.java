package com.nanxin.catbook.repository;

import com.nanxin.catbook.entity.Cat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CatRepository extends JpaRepository<Cat, Long> {

    Page<Cat> findByStatus(Cat.CatStatus status, Pageable pageable);

    List<Cat> findByStatusNot(Cat.CatStatus status);

    List<Cat> findByStatus(Cat.CatStatus status);

    @Query("SELECT c FROM Cat c WHERE " +
           "(:keyword IS NULL OR c.name LIKE %:keyword% OR c.nickname LIKE %:keyword% OR c.colourTags LIKE %:keyword%) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:location IS NULL OR c.locationArea = :location)")
    Page<Cat> search(@Param("keyword") String keyword,
                     @Param("status") Cat.CatStatus status,
                     @Param("location") String location,
                     Pageable pageable);
}