package db.warehouse.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Page<T> {

    private List<T> content;
    private int number;
    private int size;
    private int totalElements;
    private int totalPages;

    public static <T> Page<T> of(List<T> content,
                                 int number,
                                 int size,
                                 int totalElements
    ) {
        return Page.<T>builder()
                .content(content)
                .number(number)
                .size(size)
                .totalElements(totalElements)
                .totalPages((int) Math.ceil((double) totalElements / size))
                .build();
    }
}
