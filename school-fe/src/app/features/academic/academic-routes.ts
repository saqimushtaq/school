import { Routes } from "@angular/router";
import { SessionList } from "./session/session-list/session-list";

export const routes: Routes = [
  {path: '', redirectTo: '/academic/sessions', pathMatch: 'full'},
  {path: 'sessions', component: SessionList}
]
