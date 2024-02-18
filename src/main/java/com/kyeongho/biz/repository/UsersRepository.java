package com.kyeongho.biz.repository;

import com.kyeongho.biz.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * 회원 Spring Data Jpa Repository
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 13
 */
public interface UsersRepository extends JpaRepository<UsersEntity, Long> {

    Optional<UsersEntity> findByUserId(@NonNull String userId);

    @Query("select u from UsersEntity u join fetch u.incomeTaxDeductionEntities where u.userId = :userId ")
    Optional<UsersEntity> findByUserIdJoinFetchIncomeTaxDeductionEntities(@NonNull String userId);


    Optional<UsersEntity> findByNameAndRegNo(@NonNull String name, @NonNull String regNo);
}
