package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;

import jakarta.validation.Valid;
//controller vai controlar quem vai acessar, qual a rota, e quais os métodos e como os métodos vão responder ao front ou ao insomnia
@RestController
@RequestMapping("/postagens") // endereço para acessar essa controller
@CrossOrigin(origins = "*", allowedHeaders="*")//informa toda e qualquer origem pode acessar essa rota
public class PostagemController {

	//injeção de dependencias
	@Autowired
	private PostagemRepository postagemRepository;
	
	//acessado verbo get
	@GetMapping
	public ResponseEntity<List<Postagem>> getAll(){
		//respostas no formato http
		
		return ResponseEntity.ok(postagemRepository.findAll()) ;
		//find all == select * from nomedatabela
		//codigo de retorno -- corpo de retorno
		
		
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Optional<Postagem>> getById(@PathVariable Long id){
		//respostas no formato http
		
		return ResponseEntity.ok(postagemRepository.findById(id));
		//find all == select * from nomedatabela
		//codigo de retorno -- corpo de retorno
		
		
	}
	
	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo){
		//respostas no formato http
		
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
		//find all == select * from nomedatabela
		//codigo de retorno -- corpo de retorno
		
		
	}
	
	/*
	@GetMapping("/exemplo")
	public ResponseEntity<String> exemplo(){
		String ola = "Olá mundo";
		return ResponseEntity.noContent().build();
		//nesse exemplo utilizamos o build pq não vamos retornar nada no corpo da requisição
		//o .noContent vai retornar outro código de status no insomnia
		//cada status serve para indicar alguma coisa veja a documentação noContent = não contem o dado ou informação solicitada
	}
	*/
	
	@PostMapping
	public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(postagemRepository.save(postagem));
	}
	
	@PutMapping
	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) {
		return postagemRepository.findById(postagem.getId())
				.map(resposta -> ResponseEntity.status(HttpStatus.OK)
						.body(postagemRepository.save(postagem)))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		Optional<Postagem> postagem = postagemRepository.findById(id);
		
		if(postagem.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		postagemRepository.deleteById(id);				
	}
	
}
