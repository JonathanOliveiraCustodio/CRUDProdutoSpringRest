package br.edu.fateczl.CRUDProdutoSpringRest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.fateczl.CRUDProdutoSpringRest.model.dto.ProdutoDTO;
import br.edu.fateczl.CRUDProdutoSpringRest.model.entity.Produto;
import br.edu.fateczl.CRUDProdutoSpringRest.repository.IProdutoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ProdutoController implements ICRUDController<ProdutoDTO> {

    @Autowired
    IProdutoRepository pRep;

    @Override
    @GetMapping("/produto")
    public List<ProdutoDTO> lista() {
        List<Produto> produtos = pRep.findAll();
        return convListProdProdDTO(produtos);
    }

    @Override
    @GetMapping("/produto/{codigoProduto}")
    public ResponseEntity<ProdutoDTO> busca(@PathVariable(value = "codigoProduto") int cod) {
        Produto p = pRep.findById(cod).orElseThrow(() -> new ResourceNotFoundException("Código Inválido"));
        ProdutoDTO pDTO = convProdProdDTO(p);
        return ResponseEntity.ok().body(pDTO);
    }

    @Override
    @PostMapping("/produto")
    public ResponseEntity<String> adiciona(@Valid @RequestBody ProdutoDTO pDTO) {
        Produto p = convProdDTOProd(pDTO);
        pRep.save(p);
        return ResponseEntity.ok().body("Produto Adicionado com sucesso");
    }

    @Override
    @PutMapping("/produto")
    public ResponseEntity<String> atualiza(@Valid @RequestBody ProdutoDTO pDTO) {
        Produto p = convProdDTOProd(pDTO);
        pRep.save(p);
        return ResponseEntity.ok().body("Produto atualizado com sucesso");
    }

    @Override
    @DeleteMapping("/produto")
    public ResponseEntity<String> exclui(@Valid @RequestBody ProdutoDTO pDTO) {
        Produto p = convProdDTOProd(pDTO);
        pRep.delete(p);
        return ResponseEntity.ok().body("Produto excluído com sucesso");
    }

    private List<ProdutoDTO> convListProdProdDTO(List<Produto> produtos) {
        List<ProdutoDTO> produtosDTO = new ArrayList<>();
        for (Produto p : produtos) {
            ProdutoDTO pDTO = new ProdutoDTO();
            pDTO.setCodigo(p.getCodigo());
            pDTO.setNome(p.getNome());
            pDTO.setValorUnitario(p.getValorUnitario());
            pDTO.setQtdEstoque(p.getQtdEstoque());
            produtosDTO.add(pDTO);
        }
        return produtosDTO;
    }

    private ProdutoDTO convProdProdDTO(Produto p) {
        ProdutoDTO pDTO = new ProdutoDTO();
        pDTO.setCodigo(p.getCodigo());
        pDTO.setNome(p.getNome());
        pDTO.setValorUnitario(p.getValorUnitario());
        pDTO.setQtdEstoque(p.getQtdEstoque());
        return pDTO;
    }

    private Produto convProdDTOProd(ProdutoDTO pDTO) {
        Produto p = new Produto();
        p.setCodigo(pDTO.getCodigo());
        p.setNome(pDTO.getNome());
        p.setValorUnitario(pDTO.getValorUnitario());
        p.setQtdEstoque(pDTO.getQtdEstoque());
        return p;
    }
}
