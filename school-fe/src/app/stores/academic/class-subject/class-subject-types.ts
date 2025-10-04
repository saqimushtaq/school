// class-subject-types.ts

export interface ClassSubjectRequest {
  classId: number;
  subjectId: number;
  totalMarks?: number;
  passingMarks?: number;
}

export interface ClassSubjectResponse {
  id: number;
  classId: number;
  className: string;
  section: string;
  subjectId: number;
  subjectName: string;
  subjectCode: string;
  totalMarks: number;
  passingMarks: number;
  createdAt: string;
}

export interface ClassSubjectState {
  classSubjects: ClassSubjectResponse[];
  subjectsByClass: Map<number, ClassSubjectResponse[]>;
  classesBySubject: Map<number, ClassSubjectResponse[]>;
  selectedClassSubject: ClassSubjectResponse | null;
  isLoading: boolean;
  error: string | null;
}

export const initialClassSubjectState: ClassSubjectState = {
  classSubjects: [],
  subjectsByClass: new Map(),
  classesBySubject: new Map(),
  selectedClassSubject: null,
  isLoading: false,
  error: null,
};
