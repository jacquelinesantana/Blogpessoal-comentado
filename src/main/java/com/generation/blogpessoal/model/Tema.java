package com.generation.blogpessoal.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table( name = "tb_temas")
public class Tema {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "O atributo descricao deve ser informado")
	private String descricao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	//relacionamento de tema com postagem
	/*a classe atual Tema é one e a classe postagem é many
	 * ++fetch é uma propriedade que faz com que retorna as entidades relacionadas ao 
	 * fazer o select lá na repository
	 * 
	 * ++FetchType.LAZY indica que essa busca/select será do tipo preguiçosa
	 * 
	 * ++mappedBy indica que o lado proprietário do relacionamento sera a classe tema
	 * a --------postagem pertence ao tema----------------- isso ajuda o hibernate a saber 
	 * onde deve criar a chave estrangeira
	 * 
	 * +++Cascade: define o comportamento quando excluímos o tema o que deve ser feito 
	 * com as postagens desse tema excluído? o efeito cascade apagará a postagem
	 * do tema que for deletado ou irá manter conforme configurarmos aqui
	 * 
	 * ++CacadeType. Remove - define que quando o tema for deletado não faz sentido
	 * manter as postagens desse mesmo tema, então as postagens serão apagadas tbm
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy="tema", cascade = CascadeType.REMOVE)
	@JsonIgnoreProperties("tema")//evita o looping ao buscar o tema da postagem e a postagem do tema
	private List<Postagem> postagem;

	public List<Postagem> getPostagem() {
		return postagem;
	}

	public void setPostagem(List<Postagem> postagem) {
		this.postagem = postagem;
	}
	
	
	
	
	
}
