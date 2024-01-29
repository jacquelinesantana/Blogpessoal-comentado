package com.generation.blogpessoal.controller;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

//definir a porta utilizada para os testes - que sera uma porta aleatória
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)//ciclo de vida do teste definido por classe
public class UsuarioControllerTest {

	//injeção de dependencias do usuário service para ter acesso as regras de negocio de criptografia de senha e também não permitir registrar dois emails iguais no banco
	@Autowired
	private UsuarioService usuarioService;
	
	//injeção de dependencias do repository do usuário para conseguir persistir os dados no banco de dados
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	//requisições http como se estivessemos no insomnia
	
	//primeiro a ser executado
	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();//deletar todos os registros da tabela Usuario
		
		//criarmos nosso usuário root ao rodar o teste
		usuarioService.cadastrarUsuario(new Usuario (0L, "Root", "root@root.com", "rootroot",""));
	}
	
	//nosso teste que cadastra um usuário
	@Test
	@DisplayName("Cadastra um usuário")
	public void deveCriarUmUsuario() {
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>
		(new Usuario (0L, "Paulo Cezar", "paulo@email.com", "123456789",""));
		//montamos a estrutura do usuário com o objeto usuário
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST,corpoRequisicao, Usuario.class);
		//informamos como esse cadastro vai ser passado na controller do usuário, qual a rota, verbo http e qual o corpo da requisição
		
		Assertions.assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
		//aqui a gente aplica a comparação entre o que é esperado e o que realmente conseguimos ter de resultado
	}
	
	
	@Test
	@DisplayName("Não deve permitir duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {

		usuarioService.cadastrarUsuario(new Usuario(0L, 
			"Maria da Silva", "maria_silva@email.com.br", "13465278", "-"));
		//note que na linha acima estamos passando o objeto usuário diretamente na service, em seu metodo cadastrar isso força o cadastro do usuário

		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, 
			"Maria da Silva", "maria_silva@email.com.br", "13465278", "-"));
		//criamos um objeto usuário com formato httpEntity

		ResponseEntity<Usuario> corpoResposta = testRestTemplate
			.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		//passamos o endereço do controller, verbo http e o objeto com dados do usuário para a controller do usuário

		Assertions.assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
		//verificamos se o valor retornado é um bad_request note que estamos cadastrando duas vezes o mesmo email isso não pode retornar um created pq seria um erro na aplicação
	}
	
	@Test
	@DisplayName("Atualizar um Usuário")
	public void deveAtualizarUmUsuario() {

		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, 
			"Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", "-"));
		//criamos um objeto com os dados do usuário que já estão sendo persistidos no banco atraves da classe service do usuário método cadastrarUsuário

		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(), 
			"Juliana Andrews Ramos", "juliana_ramos@email.com.br", "juliana123" , "-");
		//estamos criando um novo objeto com os dados de um usuário mas com o get().getId() estamos recuperando o usuário registrado acima
		//então o objeto acima esta sendo criado com o mesmo id da requisição anterior que faz o cadastro do usuário no banco
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
		//difine o usuário anterior como sendo do tipo httpEntity, esse formato é necessário quando vamos conversar com a controller
		

		ResponseEntity<Usuario> corpoResposta = testRestTemplate
			.withBasicAuth("root@root.com", "rootroot")
			.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		//passamos a requisição com uma linha a mais, a linha do withBasicAuth esta fazendo o login diretamente na BAsicConfig da Security, por isso cadastramos o root no método start()

		Assertions.assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
		//vamos comparar a resposta que esperamos (ok) com a resposta que a requisição vai receber
	}

	@Test
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {

		usuarioService.cadastrarUsuario(new Usuario(0L, 
			"Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123", "-"));
		
		usuarioService.cadastrarUsuario(new Usuario(0L, 
			"Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123", "-"));

		ResponseEntity<String> resposta = testRestTemplate
		.withBasicAuth("root@root.com", "rootroot")
			.exchange("/usuarios/all", HttpMethod.GET, null, String.class);

		Assertions.assertEquals(HttpStatus.OK, resposta.getStatusCode());

	}
	

}