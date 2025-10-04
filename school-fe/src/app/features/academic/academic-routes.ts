import { Routes } from "@angular/router";
import { SessionList } from "./session/session-list/session-list";
import { ClassList } from "./class/class-list/class-list";

export const routes: Routes = [
  {path: '', redirectTo: '/academic/sessions', pathMatch: 'full'},
  {path: 'sessions', component: SessionList},
  {path: 'classes', component: ClassList},
]
