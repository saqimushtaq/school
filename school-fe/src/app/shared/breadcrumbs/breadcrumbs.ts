import { Component, input } from '@angular/core';

@Component({
  selector: 'app-breadcrumbs',
  imports: [],
  templateUrl: './breadcrumbs.html',
  styles: ``
})
export class Breadcrumbs {
  title = input('');
  breadcrumbItems = input<Array<{
    active?: boolean;
    label?: string;
  }>>();
}
