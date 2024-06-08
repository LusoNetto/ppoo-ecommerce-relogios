package br.com.lusonetto.ecommerce.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.lusonetto.ecommerce.models.Item;
import br.com.lusonetto.ecommerce.models.Pedido;
import br.com.lusonetto.ecommerce.repositories.PedidoRepository; 

@RestController
@RequestMapping(value = "/pedidos")
public class PedidoController {
	
	@Autowired
    private RestTemplate restTemplate;
	
    @Autowired
    private PedidoRepository pedidoRepository;

    PedidoController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }
    
    private static Long id = 1l;

    @GetMapping()
    List<Pedido> all() {
        return pedidoRepository.findAll();
    }

    @PostMapping()
    Pedido newPedido(@RequestBody Pedido newPedido) {
    	String url = "http://localhost:8080/items";
        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Item> itensCarrinho = null;
        try {
            itensCarrinho = objectMapper.readValue(response, new TypeReference<List<Item>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    	newPedido.setItems(itensCarrinho);
    	newPedido.setId(this.id+1);
    	
        return pedidoRepository.save(newPedido);
    }

    @GetMapping("/{id}")
    Pedido one(@PathVariable Long id) {

        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nenhum Pedido encontrado"));
    }

    @PutMapping("/{id}")
    Pedido replacePedido(@RequestBody Pedido newPedido, @PathVariable Long id) {

        return pedidoRepository.findById(id)
                .map(Pedido -> {
                    Pedido.setItems(newPedido.getItems());
                    Pedido.setCliente(newPedido.getCliente());
                    return pedidoRepository.save(Pedido);
                })
                .orElseGet(() -> {
                    newPedido.setId(id);
                    return pedidoRepository.save(newPedido);
                });
    }

    @DeleteMapping("/{id}")
    void deletePedido(@PathVariable Long id) {
        pedidoRepository.deleteById(id);
    }
}
