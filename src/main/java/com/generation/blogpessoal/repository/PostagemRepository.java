package com.generation.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.generation.blogpessoal.model.Postagem;
//JPA da o poder da repository comunicar-se com o banco de dados 
public interface PostagemRepository extends JpaRepository <Postagem, Long> {
	
	public List<Postagem> findAllByTituloContainingIgnoreCase(@Param("titulo") String titulo);
}
