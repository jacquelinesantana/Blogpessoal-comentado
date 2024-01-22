package com.generation.blogpessoal.security;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/*
 * classe gerenciada pelo spring boot
 */
@Component
public class JwtService {
	//essa classe vai formar o token no momento do login

	/*
	 * atributo é um final é uma constante, seu valor não pode ser modificado
	 * 
	 * static é um modificador para o atributo que deve estar associado apenas
	 *  e exclusivamente a esta classe, é uma variável da classe e não um objeto
	 *  
	 *  constantes devem ser nomeada em caixa alta
	 *  
	 *  gerar a chave no site: https://www.keygen.io/ como SHA 256
	 */
	public static final String SECRET  = "c5326af908a39fb6ca4d7ef0c5648d5c3900923f6c89b78d634157231c54e904";
	
	
	private Key getSignKey() {
		/*
		 * o getSignKey vai aplicar o base64.decoder no SECRET que é a nossa chave
		 * esse dado será do tipo byte
		 */
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);		
		/*
		 * vamos retornar a keyBytes codificado pelo algoritmo hmac sha256 que é a criptografia
		 * https://pt.wikipedia.org/wiki/HMAC
		 * HMAC(código de autenticação de mensagem com chave hash) - criptografia que usa a chave
		 * Sha - tipo de chave SHA significa secure hash algorithm (algoritmo de hash seguro). 
		 * Funções hash criptográficas são operações matemáticas executadas em dados digitais.
		 * 
		 */
		return Keys.hmacShaKeyFor(keyBytes);	}
	
	/*
	 * extractAllClaims vai retornar todos os claims inseridos na Payload(parte do Token JWT)
	 * 
	 * exe: de claim
	 * 	{
		"sub": "admin@email.com.br"
		}
	 */
	private Claims extractAllClaims(String token) {
		/*
		 * parserBuilder vai instanciar a interface JWT
		 * 
		 * setSigningKey(getSignKey()).build() pertence a interface JwtParserBuilder e verifica
		 * se a assinatura do token é valida
		 * 
		 * o metodo parseClaimsJws(token).getBody() da interface JwtParser extrai todas as claims
		 * do corpo do token e retorna todas as informações encontradas.
		 */
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey()).build()
				.parseClaimsJws(token).getBody();
	}
	
	/*extractClaim
	 * 
	 * exemplo:extractClaim(token, Claims::getSubject);
	 * 
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
/*
 * Cria um Objeto final, da Interface Claims, que receberá a execução do Método 
 * anterior, aquele que tem as informações do token
 */
		final Claims claims = extractAllClaims(token);
		
		/*
		 *retorna uma claim específica, inserida no Payload do Token JWT
		 */
		return claimsResolver.apply(claims);
	}
	
	
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
		//estamos usando o metodo anterior para buscar apenas o subject da claim, que no caso
		//é o email ou (atributo usuario) 
	}
	
	
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
		/*
		 * recupera data em que o token expira
		 */
	}
	
	
	private Boolean isTokenExpired(String token) {
		//before== antes então a data atual deve ser antes da data em que expira a validade do
		//retorna se o token expirou ou não note que estamos chamando o método anterior que peda data em quem o token expira
		return extractExpiration(token).before(new Date());
	}
	
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		/*
		 * valida o usuário que fez o login e que esta guardado na userDetails é igual
		 * ao usuário que é dono do token
		 * e também precisa não estar expirado o token
		 * aqui estamos validando o token isso evita que um invasor tente usar o token de terceiros
		 */
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));		
	}
	
	private String createToken(Map<String, Object> claims, String userName) {
		//aqui vamos criar o token
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(userName)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
		/*
		 * O Método builder(), da Classe Jwts é responsável por criar o Token, a partir dos 
		 * Métodos inseridos logo abaixo, que contém os detalhes da construção do Token JWT.
		 * 
		 * setClaims(claims), da Classe Jwts é responsável por inserir as claims personalizadas 
		 * no Payload do Token JWT.
		 * 
		 * .setSubject(userName), da Classe Jwts é responsável por inserir a claim sub (subject), 
		 * preenchida com o usuario (e-mail), no Payload do Token JWT
		 * 
		 * .setIssuedAt(new Date(System.currentTimeMillis())), da Classe Jwts é responsável por 
		 * inserir a claim iat (issued at - data e hora da criação)
		 * 
		 * .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)), da Classe Jwts é 
		 * responsável por inserir a claim exp (expiration - data e hora da expiração)
		 * 
		 * 

		    Como chegamos no valor de 60 minutos?
		    1000 🡪 1 ms (milissegundo)
		    1000 * 60 = 60.000 ms (milissegundos) 🡪 1 m (minuto)
		    60.000 * 60 = 3.600.000 ms (milissegundos) 🡪 60 m (minutos) 🡪 1 h (hora)
		    1000 * 60 * 60 = 3.600.000 milissegundos 🡪 1 hora
		    
		    .signWith(getSignKey(), SignatureAlgorithm.HS256).compact(), da Classe Jwts, é responsável 
		    por inserir a assinatura do Token (Método getSignKey()) e o Algoritmo de Encriptação do 
		    Token JWT (HMAC SHA256 - HS256) do Token JWT. O Método .compact() finaliza a criação do 
		    Token JWT e o serializa em uma String compacta e segura para URL, de acordo com as regras 
		    do JWT.

		 */
	}
	
	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		/*
		 * HashMap, da Collection Map, armazena os objetos em forma de par chave-valor. Além disso, 
		 * a implementação HashMap não mantém os Objetos ordenados, chaves duplicadas não são 
		 * permitidas e permite vários Objetos com valores nulos
		 */
		
		return createToken(claims, userName);		
		/*
		 * Retorna a execução do Método createToken(claims, userName), que criará o Token JWT.
		 */
	}
	
}
