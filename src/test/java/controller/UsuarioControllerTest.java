package controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

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

import com.example.blog.model.Usuario;
import com.example.blog.repository.UsuarioRepository;
import com.example.blog.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll
	void start(){
		usuarioRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Root", "root@root.com", "rootroot", " "));
	}
	
	@Test
	@DisplayName("Cadastra usuário")
	public void deveCriarUsuario() {
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>( new Usuario(0L,
				"Matheus Sousa", "matheus@email.com", "123", " "));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
				
	}
	
	@Test
	@DisplayName("Impede usuário duplicado")
	public void naoDeveDuplicarUsuario() {
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Matheus Sousa", "matheus@email.com", "123", " "));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>( new Usuario(0L,
				"Matheus Sousa", "matheus@email.com", "123", " "));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
		
	}
	
	@Test
	@DisplayName("Atualiza usuário")
	public void deveAtualizarUsuario () {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
				"Matheus", "matheus@email.com", "123", " "));
		
		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(),
				"Matheus Sousa", "matheus@email.com", "123", " ");
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	
	@Test
	@DisplayName("Mostra todos usuários")
	public void deveMostrarTodosUsers() {
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Matheus Sousa", "matheus@email.com", "123", " "));
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Mariana Sousa", "mariana@email.com", "123", " "));
		
		ResponseEntity<String> resposta = testRestTemplate
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
}
