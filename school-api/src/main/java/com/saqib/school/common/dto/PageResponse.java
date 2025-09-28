package com.saqib.school.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
  private List<T> content;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
  private boolean first;
  private boolean last;
  private boolean hasNext;
  private boolean hasPrevious;


  public static <T> PageResponse<T> from(Page<T> page) {
    return PageResponse.<T>builder()
      .content(page.getContent())
      .page(page.getNumber())
      .size(page.getSize())
      .first(page.isFirst())
      .last(page.isLast())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .totalPages(page.getTotalPages())
      .totalElements(page.getTotalElements())
      .build();
  }
}
