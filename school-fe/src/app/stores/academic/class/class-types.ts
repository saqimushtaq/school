// school-class-types.ts

import { defaultPagination, Pagination } from "../../common-types";

export interface ClassRequest {
  sessionId: number;
  className: string;
  section?: string;
  capacity?: number;
}

export interface ClassResponse {
  id: number;
  sessionId: number;
  sessionName: string;
  className: string;
  section: string;
  capacity: number;
  isActive: boolean;
  displayName: string;
  createdAt: string;
  updatedAt: string;
}

export interface ClassState {
  classes: ClassResponse[];
  activeClasses: ClassResponse[];
  selectedClass: ClassResponse | null;
  isLoading: boolean;
  error: string | null;
  pagination: Pagination;
}

export const initialClassState: ClassState = {
  classes: [],
  activeClasses: [],
  selectedClass: null,
  isLoading: false,
  error: null,
  pagination: defaultPagination,
};
