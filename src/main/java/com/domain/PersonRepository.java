package com.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<PersonModel, Long>
{ // A Repository in JPA is used to interact with the database. 

}
