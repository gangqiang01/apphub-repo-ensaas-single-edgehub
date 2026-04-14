package com.m2m.management.repository;

import com.m2m.management.entity.Product;
import com.m2m.management.entity.ProductConcern;
import com.m2m.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface IProductConcernRepository extends JpaRepository<ProductConcern, Long> {


    ProductConcern findByProductnameAndUser(String productname, User user);

    List<ProductConcern> findByUser(User user);

    long countByUser(User user);

    List<ProductConcern> findByProductname(String productname);

    long countByProductname(String productname);

    @Transactional
    @Modifying
    @Query(value = "delete from ProductConcern where productname = ?1")
    void deleteByProductname(String productname);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ProductConcern set productname = ?1 where productname = ?2")
    void updateByProductname(String productname, String oldProductname);
}
