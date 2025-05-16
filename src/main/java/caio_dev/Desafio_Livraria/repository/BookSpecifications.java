package caio_dev.Desafio_Livraria.repository;

import caio_dev.Desafio_Livraria.entity.Book;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BookSpecifications {

    public static Specification<Book> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    public static Specification<Book> searchBooks(String title, String author, String genre, Integer publicationYear) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Sempre aplicar o filtro de soft delete
            predicates.add(notDeleted().toPredicate(root, query, cb));
            // Filtros opcionais
            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (author != null && !author.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%"));
            }
            if (genre != null && !genre.isEmpty()) {
                predicates.add(cb.equal(root.get("genre"), genre));
            }
            if (publicationYear != null) {
                predicates.add(cb.equal(root.get("publicationYear"), publicationYear));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}