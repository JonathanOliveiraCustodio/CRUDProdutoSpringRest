package br.edu.fateczl.CRUDProdutoSpringRest.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "produto")

@NamedNativeQuery(name = "Produto.findByQtd",query = "SELECT dbo.fn_quantidadeEstoque(?1) AS quantidade",
	    resultSetMapping = "QuantidadeResultMapping"
	)
	@SqlResultSetMapping(
	    name = "QuantidadeResultMapping",
	    classes = {
	        @ConstructorResult(
	            targetClass = Integer.class,
	            columns = {
	                @ColumnResult(name = "quantidade", type = Integer.class)
	            }
	        )
	    }
	)

@NamedNativeQuery(name = "Produto.findAllProdutos", query = "SELECT * FROM fn_produtosEstoque(?1)", resultClass = Produto.class)

public class Produto {

	@Id
	@Column(name = "codigo", nullable = false)
	private int codigo;

	@Column(name = "nome",length = 50, nullable = false)
	private String nome;
	
	@Column(name = "valorUnitario", nullable = false)
	private float valorUnitario;
	
	@Column(name = "qtdEstoque", nullable = false)
	private int qtdEstoque;
	
}
