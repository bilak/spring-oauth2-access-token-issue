package com.github.bilak.jpa.repository;

import com.github.bilak.jpa.model.Accessor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by lvasek on 16/10/2016.
 */
@Repository
public interface AccessorRepository extends PagingAndSortingRepository<Accessor, String> {

	Optional<Accessor> findOneByEmail(String email);

	Optional<Accessor> findOneById(String id);
}
