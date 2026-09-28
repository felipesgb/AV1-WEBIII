package com.autobots.automanager.controles;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.servicos.TelefoneServico;

@RestController
@RequestMapping({"/telefone", "/telefones"})
public class TelefoneControle {
    private final TelefoneServico servico;
    public TelefoneControle(TelefoneServico servico) { this.servico = servico; }
    @GetMapping({"", "/telefones"})
    public List<Telefone> listar() { return servico.listar(); }
    @GetMapping({"/{id}", "/telefone/{id}"})
    public Telefone buscar(@PathVariable Long id) { return servico.buscar(id); }
    @PostMapping({"", "/cadastro"})
    public ResponseEntity<Telefone> criar(@RequestBody Telefone item, @RequestParam(required = false) Long clienteId) {
        Telefone criado = servico.criar(item, clienteId);
        return ResponseEntity.created(URI.create("/telefones/" + criado.getId())).body(criado);
    }
    @PutMapping("/{id}")
    public Telefone atualizar(@PathVariable Long id, @RequestBody Telefone item) { return servico.atualizar(id, item); }
    @PutMapping("/atualizar")
    public Telefone atualizarLegado(@RequestBody Telefone item) { return servico.atualizar(idObrigatorio(item), item); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) { servico.excluir(id); return ResponseEntity.noContent().build(); }
    @DeleteMapping("/excluir")
    public ResponseEntity<Void> excluirLegado(@RequestBody Telefone item) { return excluir(idObrigatorio(item)); }
    private Long idObrigatorio(Telefone item) {
        if (item.getId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o ID");
        return item.getId();
    }
}
