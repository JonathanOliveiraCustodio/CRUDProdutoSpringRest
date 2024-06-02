package br.edu.fateczl.CRUDProdutoSpringRest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import br.edu.fateczl.CRUDProdutoSpringRest.model.entity.Produto;

public interface IProdutoRepository extends JpaRepository<Produto, Integer>{ 
	
	@Query(name = "Produto.findAllProdutos", nativeQuery = true)
    List<Produto> findAllProdutos(int valor);
	
	
	
	@Query(name = "Produto.findByQtd", nativeQuery = true)
	int findByQtd(int qtd);

}
