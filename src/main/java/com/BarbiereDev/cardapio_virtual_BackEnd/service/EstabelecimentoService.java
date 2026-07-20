package com.BarbiereDev.cardapio_virtual_BackEnd.service;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.EstabelecimentoRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.EstabelecimentoResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Estabelecimento;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.EstabelecimentoRepository;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<EstabelecimentoResponse> findAll() {
        return estabelecimentoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public EstabelecimentoResponse findById(Long id) {
        return estabelecimentoRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));
    }

    public EstabelecimentoResponse findBySlug(String slug) {
        return estabelecimentoRepository.findBySlug(slug)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));
    }

    @Transactional
    public EstabelecimentoResponse create(EstabelecimentoRequest request, Usuario criador) {
        String slug = request.getSlug() != null ? request.getSlug() : gerarSlug(request.getNome());

        if (estabelecimentoRepository.existsBySlug(slug)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slug já cadastrado");
        }

        Estabelecimento estabelecimento = Estabelecimento.builder()
                .nome(request.getNome())
                .slug(slug)
                .criador(criador)
                .colaboradores(new HashSet<>(Set.of(criador)))
                .build();

        estabelecimentoRepository.save(estabelecimento);
        return toResponse(estabelecimento);
    }

    @Transactional
    public EstabelecimentoResponse update(Long id, EstabelecimentoRequest request, Usuario usuarioLogado) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        if (!estabelecimento.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode alterar o estabelecimento");
        }

        if (request.getNome() != null) {
            estabelecimento.setNome(request.getNome());
        }

        if (request.getSlug() != null && !request.getSlug().equals(estabelecimento.getSlug())) {
            if (estabelecimentoRepository.existsBySlug(request.getSlug())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Slug já cadastrado");
            }
            estabelecimento.setSlug(request.getSlug());
        }

        estabelecimentoRepository.save(estabelecimento);
        return toResponse(estabelecimento);
    }

    @Transactional
    public void delete(Long id, Usuario usuarioLogado) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        if (!estabelecimento.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode excluir o estabelecimento");
        }

        estabelecimentoRepository.delete(estabelecimento);
    }

    @Transactional
    public EstabelecimentoResponse adicionarColaborador(Long estabelecimentoId, Long usuarioId, Usuario usuarioLogado) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        if (!estabelecimento.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode gerenciar colaboradores");
        }

        Usuario colaborador = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        estabelecimento.getColaboradores().add(colaborador);
        estabelecimentoRepository.save(estabelecimento);
        return toResponse(estabelecimento);
    }

    @Transactional
    public EstabelecimentoResponse removerColaborador(Long estabelecimentoId, Long usuarioId, Usuario usuarioLogado) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        if (!estabelecimento.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode gerenciar colaboradores");
        }

        if (usuarioId.equals(estabelecimento.getCriador().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível remover o criador como colaborador");
        }

        estabelecimento.getColaboradores().removeIf(u -> u.getId().equals(usuarioId));
        estabelecimentoRepository.save(estabelecimento);
        return toResponse(estabelecimento);
    }

    public Set<EstabelecimentoResponse.UsuarioResumo> listarColaboradores(Long id) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        return estabelecimento.getColaboradores().stream()
                .map(this::toUsuarioResumo)
                .collect(Collectors.toSet());
    }

    public EstabelecimentoResponse toResponse(Estabelecimento estabelecimento) {
        return EstabelecimentoResponse.builder()
                .id(estabelecimento.getId())
                .nome(estabelecimento.getNome())
                .slug(estabelecimento.getSlug())
                .criador(toUsuarioResumo(estabelecimento.getCriador()))
                .colaboradores(estabelecimento.getColaboradores().stream()
                        .map(this::toUsuarioResumo)
                        .collect(Collectors.toSet()))
                .createdAt(estabelecimento.getCreatedAt())
                .updatedAt(estabelecimento.getUpdatedAt())
                .build();
    }

    private EstabelecimentoResponse.UsuarioResumo toUsuarioResumo(Usuario usuario) {
        return EstabelecimentoResponse.UsuarioResumo.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .build();
    }

    private String gerarSlug(String nome) {
        String normalizado = Normalizer.normalize(nome, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();

        String slug = normalizado;
        int contador = 1;
        while (estabelecimentoRepository.existsBySlug(slug)) {
            slug = normalizado + "-" + contador++;
        }
        return slug;
    }
}
