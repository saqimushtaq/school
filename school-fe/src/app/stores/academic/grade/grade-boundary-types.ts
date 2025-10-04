// grade-boundary-types.ts

export interface GradeBoundary {
  id: number;
  grade: string;
  minPercentage: number;
  maxPercentage: number;
  isPassing: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface GradeBoundaryRequest {
  grade: string;
  minPercentage: number;
  maxPercentage: number;
  isPassing: boolean;
}

export interface GradeBoundaryState {
  gradeBoundaries: GradeBoundary[];
  selectedGradeBoundary: GradeBoundary | null;
  isLoading: boolean;
  error: string | null;
}

export const initialGradeBoundaryState: GradeBoundaryState = {
  gradeBoundaries: [],
  selectedGradeBoundary: null,
  isLoading: false,
  error: null,
};
