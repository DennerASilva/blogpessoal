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
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController // Define ao Spring que essa Classe é uma Controller
@RequestMapping("/postagens") // Define qual endpoint vai ser tratado por essa Classe
@CrossOrigin(origins = "*", allowedHeaders = "*") // Libera o acesso a qualquer front que não
public class PostagemController {
	
	@Autowired // O Spring dá autonomia para a Interface poder invocar os métodos
	private PostagemRepository postagemRepository;
	
	@Autowired
	private TemaRepository temaRepository;
	
	@GetMapping // Indica que esse método é chamado em Verbos/Métodos HTTP do tipo Get
	public ResponseEntity<List<Postagem>> getAll() {
		return ResponseEntity.ok(postagemRepository.findAll()); //SELECT * FROM tb_postagens
	}
	
	@GetMapping("/{id}") // postagens/2 quando ID = 2. 
	public ResponseEntity<Postagem> getById(@PathVariable Long id) { //id = 2
		return postagemRepository.findById(id) //fazendo e guardando o resultado em um Optional 
				.map(resposta -> ResponseEntity.ok(resposta)) // o .map faz a validação (if) se a busca foi concluída. E caso sim, retorna o status 200 (OK)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Não foi encontrado")); // Caso a validação não seja verdade (else), retorna o status 404 (Não encontrado) e uma mensagem personalizada
			
		
				/* A forma como está na documentação (sem a mensagem personalizada)
				 * 
				 * .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)).build()); 
				 */
				
				/* Como seria usando a estrutura do Optional, junto com If-Else:
				 * 
				 * Optional<Postagem> PostagemOpt = postagemRepository.findById(id);
				if(PostagemOpt.isEmpty()) {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Postagem não encontrada.");
				}
				return ResponseEntity.status(HttpStatus.OK).body(PostagemOpt.get()); */
	}
	
	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo){			
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
	}
	
	@PostMapping
	public ResponseEntity<Postagem> post (@Valid @RequestBody Postagem postagem) { // Postagem = {"titulo" = "ababuble ..."}
		if (temaRepository.existsById(postagem.getTema().getId()))
			return ResponseEntity.status(HttpStatus.CREATED) 
					.body(postagemRepository.save(postagem)); // Postagem = {id, data, titulo, texto...}
		
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Tema não existe!");
	}
	
	@PutMapping
	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem){
		if (postagemRepository.existsById(postagem.getId())) {
		
			if (temaRepository.existsById(postagem.getTema().getId()))
				return ResponseEntity.status(HttpStatus.OK)
						.body(postagemRepository.save(postagem));
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Tema não existe!");
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
		
	
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		Optional<Postagem> postagemOpt = postagemRepository.findById(id);
		
		if(postagemOpt.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Postagem não encontrada");
		postagemRepository.deleteById(id);		
	}	
	
}