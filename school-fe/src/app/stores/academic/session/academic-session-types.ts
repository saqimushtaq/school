// academic-session-types.ts

import { defaultPagination, Pagination } from "../../common-types";

export enum SessionStatus {
  INACTIVE = 'INACTIVE',
  ACTIVE = 'ACTIVE',
  UPCOMING = 'UPCOMING',
  ARCHIVED = 'ARCHIVED'
}

export interface AcademicSessionRequest {
  sessionName: string;
  startDate: string; // ISO date string
  endDate: string;   // ISO date string
}

export interface AcademicSessionResponse {
  id: number;
  sessionName: string;
  startDate: string;
  endDate: string;
  status: SessionStatus;
  createdAt: string;
  updatedAt: string;
}

export interface AcademicSessionState {
  sessions: AcademicSessionResponse[];
  activeSession: AcademicSessionResponse | null;
  upcomingSession: AcademicSessionResponse | null;
  selectedSession: AcademicSessionResponse | null;
  isLoading: boolean;
  error: string | null;
  pagination: Pagination;
}

export const initialAcademicSessionState: AcademicSessionState = {
  sessions: [],
  activeSession: null,
  upcomingSession: null,
  selectedSession: null,
  isLoading: false,
  error: null,
  pagination: defaultPagination,
};
