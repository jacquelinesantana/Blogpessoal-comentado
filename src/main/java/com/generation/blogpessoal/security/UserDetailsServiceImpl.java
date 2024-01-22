package com.generation.blogpessoal.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;


/*
 * classe de serviço precisa implementar regras de negocio
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	/*
	 * injeção de dependencias da repository de usuario, classe que pode se comunicar com 
	 * o banco de dados
	 */
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {		// método da UserDetailsService de implementação obrigatória
		
		/*
		 * esse optional esta recebendo um objeto do tipo usuario, que recebe o retorno do método
		 * findByUsuario da repository, ou seja ele busca pelo usuario no banco de dados se o mesmo
		 * existe popula esse objeto senão vai popular como nullo o optional
		 * 
		 * o optional esta aqui, pois caso não existir esse usuario no banco não teremos 
		 * que tratar um erro de null pointer
		 */
		Optional<Usuario> usuario = usuarioRepository.findByUsuario(userName);		
		/*
		 * se o usuario existe e esta populado nos vamos instanciar a classe UserDetailsImpl
		 * caso não vamos retornar um status Forbidden 
		 * - indica que o servidor entendeu o pedido, mas se recusa a autorizá-lo
		 */
		if (usuario.isPresent())
			return new UserDetailsImpl(usuario.get());
		else
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		
	}
	
}
