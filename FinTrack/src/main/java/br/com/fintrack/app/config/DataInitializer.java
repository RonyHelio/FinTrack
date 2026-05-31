package br.com.fintrack.app.config;

import br.com.fintrack.app.entity.Categoria;
import br.com.fintrack.app.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional
    public void run(String... args) {
        List<Categoria> categoriasGlobais = categoriaRepository.findByUsuarioIsNull();

        if (categoriasGlobais.isEmpty()) {
            log.info("Nenhuma categoria global encontrada. Inserindo categorias padrão...");

            List<Categoria> categorias = List.of(
                    criarCategoria("Alimentação", "🍔"),
                    criarCategoria("Transporte", "🚗"),
                    criarCategoria("Moradia", "🏠"),
                    criarCategoria("Lazer", "🎮"),
                    criarCategoria("Saúde", "🏥"),
                    criarCategoria("Educação", "📚"),
                    criarCategoria("Salário", "💰"),
                    criarCategoria("Investimentos", "📈")
            );

            categoriaRepository.saveAll(categorias);
            log.info("{} categorias globais inseridas com sucesso.", categorias.size());
        } else {
            log.info("{} categorias globais já existem. Nenhuma inserção necessária.", categoriasGlobais.size());
        }
    }

    private Categoria criarCategoria(String nome, String icone) {
        return Categoria.builder()
                .usuario(null)
                .nome(nome)
                .icone(icone)
                .build();
    }
}
