package by.sakuuj.digital.chief.testproj.paging;

import lombok.Builder;

import java.util.List;
import java.util.function.Function;

@Builder
public record PagedResponse<T>(List<T> content, int pageNumber, int pageSize, int totalCount) {

    public <R> PagedResponse<R> map(Function<T, R> mapper) {

        List<R> mappedContent = content.stream()
                .map(mapper)
                .toList();

        return PagedResponse.<R>builder()
                .content(mappedContent)
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .totalCount(totalCount)
                .build();
    }
}
