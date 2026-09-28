package com.autobots.automanager.controles;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.servicos.ClienteServico;

@RestController
@RequestMapping({"/cliente", "/clientes"})
public class ClienteControle {
    private final ClienteServico servico;
    public ClienteControle(ClienteServico servico) { this.servico = servico; }
    @GetMapping({"", "/clientes"})
    public List<Cliente> listar() { return servico.listar(); }
    @GetMapping({"/{id}", "/cliente/{id}"})
    public Cliente buscar(@PathVariable Long id) { return servico.buscar(id); }
    @PostMapping({"", "/cadastro"})
    public ResponseEntity<Cliente> criar(@RequestBody Cliente item) {
        Cliente criado = servico.criar(item);
        return ResponseEntity.created(URI.create("/clientes/" + criado.getId())).body(criado);
    }
    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Long id, @RequestBody Cliente item) { return servico.atualizar(id, item); }
    @PutMapping("/atualizar")
    public Cliente atualizarLegado(@RequestBody Cliente item) { return servico.atualizar(idObrigatorio(item), item); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) { servico.excluir(id); return ResponseEntity.noContent().build(); }
    @DeleteMapping("/excluir")
    public ResponseEntity<Void> excluirLegado(@RequestBody Cliente item) { return excluir(idObrigatorio(item)); }
    private Long idObrigatorio(Cliente item) {
        if (item.getId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o ID");
        return item.getId();
    }
}
