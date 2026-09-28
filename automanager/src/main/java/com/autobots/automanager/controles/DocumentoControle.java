package com.autobots.automanager.controles;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.servicos.DocumentoServico;

@RestController
@RequestMapping({"/documento", "/documentos"})
public class DocumentoControle {
    private final DocumentoServico servico;
    public DocumentoControle(DocumentoServico servico) { this.servico = servico; }
    @GetMapping({"", "/documentos"})
    public List<Documento> listar() { return servico.listar(); }
    @GetMapping({"/{id}", "/documento/{id}"})
    public Documento buscar(@PathVariable Long id) { return servico.buscar(id); }
    @PostMapping({"", "/cadastro"})
    public ResponseEntity<Documento> criar(@RequestBody Documento item, @RequestParam(required = false) Long clienteId) {
        Documento criado = servico.criar(item, clienteId);
        return ResponseEntity.created(URI.create("/documentos/" + criado.getId())).body(criado);
    }
    @PutMapping("/{id}")
    public Documento atualizar(@PathVariable Long id, @RequestBody Documento item) { return servico.atualizar(id, item); }
    @PutMapping("/atualizar")
    public Documento atualizarLegado(@RequestBody Documento item) { return servico.atualizar(idObrigatorio(item), item); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) { servico.excluir(id); return ResponseEntity.noContent().build(); }
    @DeleteMapping("/excluir")
    public ResponseEntity<Void> excluirLegado(@RequestBody Documento item) { return excluir(idObrigatorio(item)); }
    private Long idObrigatorio(Documento item) {
        if (item.getId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o ID");
        return item.getId();
    }
}
