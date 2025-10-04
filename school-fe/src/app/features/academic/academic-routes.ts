import { Routes } from "@angular/router";
import { SessionList } from "./session/session-list/session-list";
import { ClassList } from "./class/class-list/class-list";
import { SubjectList } from "./subject/subject-list/subject-list";
import { ClassSubjectList } from "./class-subject/class-subject-list/class-subject-list";
import { GradeBoundaryList } from "./grade/grade-boundary-list/grade-boundary-list";

export const routes: Routes = [
  {path: '', redirectTo: '/academic/sessions', pathMatch: 'full'},
  {path: 'sessions', component: SessionList},
  {path: 'classes', component: ClassList},
  {path: 'subjects', component: SubjectList},
  {path: 'class-subjects', component: ClassSubjectList},
  {path: 'grade-boundary', component: GradeBoundaryList},
]
