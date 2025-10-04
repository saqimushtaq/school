// subject-types.ts

import { defaultPagination, Pagination } from "../../common-types";

export interface SubjectRequest {
  subjectName: string;
  subjectCode: string;
  isActive?: boolean;
}

export interface SubjectResponse {
  id: number;
  subjectName: string;
  subjectCode: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface SubjectState {
  subjects: SubjectResponse[];
  activeSubjects: SubjectResponse[];
  selectedSubject: SubjectResponse | null;
  isLoading: boolean;
  error: string | null;
  pagination: Pagination;
}

export const initialSubjectState: SubjectState = {
  subjects: [],
  activeSubjects: [],
  selectedSubject: null,
  isLoading: false,
  error: null,
  pagination: defaultPagination,
};
