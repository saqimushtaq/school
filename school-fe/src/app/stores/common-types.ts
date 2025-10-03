// common-types.ts

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
  errors?: any;
  timestamp: string;
}

export type Pagination = {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
};


export interface PageResponse<T> extends Pagination {
  content: T[];
}

export function toPagination<T>(page: PageResponse<T>): Pagination {
  const {content, ...pagination} = page
  return pagination;
}

export const defaultPagination: Pagination = {
  page: 0,
  size: 10,
  totalElements: 0,
  totalPages: 0,
  first: true,
  last: true,
  hasNext: false,
  hasPrevious: false,
}
