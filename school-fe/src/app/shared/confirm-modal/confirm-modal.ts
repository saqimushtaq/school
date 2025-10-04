import { Component, CUSTOM_ELEMENTS_SCHEMA, inject, output } from '@angular/core';
import { defineElement } from '@lordicon/element';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-confirm-modal',
  imports: [],
  templateUrl: './confirm-modal.html',
  styles: ``,
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ConfirmModal {

  modal = inject(NgbActiveModal)

  icon = 'bx bx-trash bx-lg text-danger'

  confirmButtonText = 'Yes, Delete it!'

  confirmButtonClass = 'btn w-sm btn-danger'

  message = 'Are you sure you want to remove this record ?'

  constructor(){
    defineElement()
  }


}
