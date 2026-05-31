package br.com.fintrack.app.service;

import br.com.fintrack.app.dto.TransacaoRequestDTO;
import br.com.fintrack.app.dto.TransacaoResponseDTO;
import br.com.fintrack.app.entity.Categoria;
import br.com.fintrack.app.entity.TipoTransacao;
import br.com.fintrack.app.entity.Transacao;
import br.com.fintrack.app.entity.Usuario;
import br.com.fintrack.app.exception.ResourceNotFoundException;
import br.com.fintrack.app.repository.CategoriaRepository;
import br.com.fintrack.app.repository.TransacaoRepository;
import br.com.fintrack.app.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional
    public TransacaoResponseDTO criar(TransacaoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", dto.usuarioId()));

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", dto.categoriaId()));

        TipoTransacao tipoTransacao = TipoTransacao.fromValor(dto.tipo());

        Transacao transacao = Transacao.builder()
                .usuario(usuario)
                .categoria(categoria)
                .valor(dto.valor())
                .tipo(tipoTransacao)
                .data(dto.data())
                .descricao(dto.descricao())
                .build();

        Transacao salva = transacaoRepository.save(transacao);
        return toResponseDTO(salva);
    }

    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> listarPorUsuario(UUID usuarioId) {
        return transacaoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> listarPorUsuarioEPeriodo(UUID usuarioId, LocalDate inicio, LocalDate fim) {
        return transacaoRepository.findByUsuarioIdAndDataBetween(usuarioId, inicio, fim)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransacaoResponseDTO buscarPorId(UUID id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação", "id", id));
        return toResponseDTO(transacao);
    }

    @Transactional
    public TransacaoResponseDTO atualizar(UUID id, TransacaoRequestDTO dto) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação", "id", id));

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", dto.usuarioId()));

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", dto.categoriaId()));

        TipoTransacao tipoTransacao = TipoTransacao.fromValor(dto.tipo());

        transacao.setUsuario(usuario);
        transacao.setCategoria(categoria);
        transacao.setValor(dto.valor());
        transacao.setTipo(tipoTransacao);
        transacao.setData(dto.data());
        transacao.setDescricao(dto.descricao());

        Transacao atualizada = transacaoRepository.save(transacao);
        return toResponseDTO(atualizada);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!transacaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transação", "id", id);
        }
        transacaoRepository.deleteById(id);
    }

    private TransacaoResponseDTO toResponseDTO(Transacao transacao) {
        return new TransacaoResponseDTO(
                transacao.getId(),
                transacao.getUsuario().getId(),
                transacao.getCategoria().getId(),
                transacao.getCategoria().getNome(),
                transacao.getValor(),
                transacao.getTipo().getValor(),
                transacao.getData(),
                transacao.getDescricao()
        );
    }
}
