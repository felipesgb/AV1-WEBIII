package com.autobots.automanager.controles;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.servicos.EnderecoServico;

@RestController
@RequestMapping({"/endereco", "/enderecos"})
public class EnderecoControle {
    private final EnderecoServico servico;
    public EnderecoControle(EnderecoServico servico) { this.servico = servico; }
    @GetMapping({"", "/enderecos"})
    public List<Endereco> listar() { return servico.listar(); }
    @GetMapping({"/{id}", "/endereco/{id}"})
    public Endereco buscar(@PathVariable Long id) { return servico.buscar(id); }
    @PostMapping({"", "/cadastro"})
    public ResponseEntity<Endereco> criar(@RequestBody Endereco item, @RequestParam(required = false) Long clienteId) {
        Endereco criado = servico.criar(item, clienteId);
        return ResponseEntity.created(URI.create("/enderecos/" + criado.getId())).body(criado);
    }
    @PutMapping("/{id}")
    public Endereco atualizar(@PathVariable Long id, @RequestBody Endereco item) { return servico.atualizar(id, item); }
    @PutMapping("/atualizar")
    public Endereco atualizarLegado(@RequestBody Endereco item) { return servico.atualizar(idObrigatorio(item), item); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) { servico.excluir(id); return ResponseEntity.noContent().build(); }
    @DeleteMapping("/excluir")
    public ResponseEntity<Void> excluirLegado(@RequestBody Endereco item) { return excluir(idObrigatorio(item)); }
    private Long idObrigatorio(Endereco item) {
        if (item.getId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o ID");
        return item.getId();
    }
}
