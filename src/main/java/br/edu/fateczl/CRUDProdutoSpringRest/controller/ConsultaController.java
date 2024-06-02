package br.edu.fateczl.CRUDProdutoSpringRest.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import br.edu.fateczl.CRUDProdutoSpringRest.model.entity.Produto;
import br.edu.fateczl.CRUDProdutoSpringRest.repository.IProdutoRepository;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.util.JRLoader;

@Controller
public class ConsultaController {

	@Autowired
	DataSource ds; 

    @Autowired
    IProdutoRepository pRep;

    @RequestMapping(name = "consulta", value = "/consulta", method = RequestMethod.GET)
    public ModelAndView consultaGet(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
        return new ModelAndView("consulta");
    }

    @RequestMapping(name = "consulta", value = "/consulta", method = RequestMethod.POST)
    public ModelAndView processForm(@RequestParam("valorEntrada") int valorEntrada, @RequestParam("action") String action, ModelMap model) {
        
    	if ("Consultar".equals(action)) {
            return realizarConsulta(valorEntrada, model);
        } else 
        	if ("Gerar".equals(action)) {
            return new ModelAndView("forward:/produtoRelatorio?valorEntrada=" + valorEntrada);
        }
        return new ModelAndView("consulta", model);
    }

    private ModelAndView realizarConsulta(int valorEntrada, ModelMap model) {
        String erro = "";
        List<Produto> produtos = null;
        int qtdEstoque = 0;
        try {
            produtos = listarProdutos(valorEntrada);
            qtdEstoque = calcularQtdProdutos(valorEntrada);
        } catch (SQLException | ClassNotFoundException e) {
            erro = e.getMessage();
        }
        model.addAttribute("erro", erro);
        model.addAttribute("produtos", produtos);
        model.addAttribute("qtdEstoque", qtdEstoque);
        return new ModelAndView("consulta", model);
    }

    @SuppressWarnings({ "rawtypes" })
    @RequestMapping(name = "produtoRelatorio", value = "/produtoRelatorio", method = RequestMethod.POST)
    public ResponseEntity produtoPost(@RequestParam Map<String, String> allRequestParam) {
        String erro = "";
        Map<String, Object> paramRelatorio = new HashMap<String, Object>();
        paramRelatorio.put("quantidade", allRequestParam.get("valorEntrada"));
        byte[] bytes = null;

        InputStreamResource resource = null;
        HttpStatus status = null;
        HttpHeaders header = new HttpHeaders();

        try {
        	
        	Connection c = DataSourceUtils.getConnection(ds);
            
            File arquivo = ResourceUtils.getFile("classpath:reports/RelatorioProdutos.jasper");
            JasperReport report = (JasperReport) JRLoader.loadObjectFromFile(arquivo.getAbsolutePath());
            bytes = JasperRunManager.runReportToPdf(report, paramRelatorio, c);
        } catch (FileNotFoundException | JRException e) {
            e.printStackTrace();
            erro = e.getMessage();
            status = HttpStatus.BAD_REQUEST;
        } finally {
            if (erro.equals("")) {
                InputStream inputStream = new ByteArrayInputStream(bytes);
                resource = new InputStreamResource(inputStream);
                header.setContentLength(bytes.length);
                header.setContentType(MediaType.APPLICATION_PDF);
                status = HttpStatus.OK;
            }
        }
        return new ResponseEntity<>(resource, header, status);
    }

    private List<Produto> listarProdutos(int valor) throws SQLException, ClassNotFoundException {
    	List<Produto> produtos = pRep.findAllProdutos(valor);
		return produtos;
    }

    private int calcularQtdProdutos(int QtdProdutos) throws SQLException, ClassNotFoundException {
    	return pRep.findByQtd(QtdProdutos);
    	 
    }
}
