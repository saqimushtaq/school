import { Component, inject } from '@angular/core';
import { LayoutStore } from '../../stores/layout/layout.store';
import { Vertical } from "../vertical/vertical";
import { Horizontal } from "../horizontal/horizontal";
import { TwoColumn } from "../two-column/two-column";

@Component({
  selector: 'app-layout',
  imports: [Vertical, Horizontal, TwoColumn],
  templateUrl: './layout.html',
  styles: ``
})
export class Layout {

  layout = inject(LayoutStore)

}
