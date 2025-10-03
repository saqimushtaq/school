import { Component, input, output } from '@angular/core';
import { NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-pagination',
  imports: [NgbPaginationModule],
  template: `
    <div class="row justify-content-md-between align-items-md-center">
      <div class="col col-sm-5">
        <div class="dataTables_info mb-2">
          Showing {{ startIndex() }} to {{ endIndex() }} of {{ totalElements() }} entries
        </div>
      </div>

      <div class="col col-sm-5">
        <div class="text-sm-right float-sm-end listjs-pagination">
          <ngb-pagination
            [collectionSize]="totalElements()"
            [page]="page()"
            [pageSize]="pageSize()"
            (pageChange)="pageChange.emit($event)"
          />
        </div>
      </div>
    </div>
  `
})
export class Pagination {
  page = input.required<number>();
  pageSize = input.required<number>();
  totalElements = input.required<number>();
  pageChange = output<number>();

  startIndex = () => (this.page() - 1) * this.pageSize() + 1;
  endIndex = () => Math.min(this.page() * this.pageSize(), this.totalElements());
}
