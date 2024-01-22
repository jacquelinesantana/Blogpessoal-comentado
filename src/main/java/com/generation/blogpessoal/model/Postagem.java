package com.generation.blogpessoal.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//modelar os atributos e a tabela postagens no banco de dados
@Entity
@Table(name="tb_postagens")
public class Postagem {
	
	@Id //tornar esse atributo uma chave primaria
	@GeneratedValue(strategy=GenerationType.IDENTITY) //para adicionar o id como autoincrement
	private Long id;
	
	//essa anotação valida o atributo tendo um valor maximo e minimo de caracteres
	@Size(min=5, max = 100, message = "O título deve ter no minimo 5 caracteres e no maximo 100")
	@NotBlank(message = "Atributo título é obrigatório")//
	private String titulo;
	 
	@Size(min=10, max = 1000, message = "O texto deve ter no minimo 10 caracteres e no maximo 1000")
	@NotBlank(message = "Atributo texto é obrigatório")//
	private String texto;
	
	@UpdateTimestamp // pega a data do sistema ao cadastrar um registro
	private LocalDateTime data;
	
	//relacionamento com tema
	//a classe atual a postagem é many e a outra, no caso Tema é one
	@ManyToOne
	@JsonIgnoreProperties("postagem") //evitar um looping ao retornar as postagens de tema e tema da postagem
	private Tema tema;
	
	@ManyToOne
	@JsonIgnoreProperties("postagem")
	private Usuario usuario;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}
	
	


	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Tema getTema() {
		return tema;
	}

	public void setTema(Tema tema) {
		this.tema = tema;
	}

	
	
	
	
}
