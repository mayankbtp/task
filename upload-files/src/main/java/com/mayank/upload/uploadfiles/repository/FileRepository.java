package com.mayank.upload.uploadfiles.repository;

import com.mayank.upload.uploadfiles.domain.FileDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileDetails, Long> {
    @Query(value = "select f.* from filesdetails f where f.name=:name and f.is_delete = :isDelete order by version desc", nativeQuery = true)
    List<FileDetails> findByNameByOrderByVersionDesc(@Param("name") String name, @Param("isDelete") Boolean isDelete);

    //@Query(value = "select f.* from filesdetails f where f.is_delete = :isDelete order by version desc", nativeQuery = true)
    @Query(value = "Select f.*, GROUP_CONCAT( version order by version ) as versions from filesdetails f where f.is_delete = :isDelete group by f.name", nativeQuery = true)
    List<Object[]> findAllFiles(@Param("isDelete") Boolean isDelete);

    @Query(value = "select f.* from filesdetails f where f.name=:name and f.is_delete = :isDelete and f.version = :version LIMIT 0,1", nativeQuery = true)
    Optional<FileDetails> findByNameIgnoreCaseByOrderByVersionDesc(@Param("name") String name, @Param("version") String version, @Param("isDelete") boolean isDelete);
}