import { Component, inject } from '@angular/core';
import { LayoutStore } from '../../stores/layout/layout.store';
import { Topbar } from "../topbar/topbar";
import { Sidebar } from "../sidebar/sidebar";
import { TwoColumnSidebar } from "../two-column-sidebar/two-column-sidebar";
import { Footer } from "../footer/footer";
import { Rightsidebar } from "../rightsidebar/rightsidebar";
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-two-column',
  imports: [Topbar, Sidebar, TwoColumnSidebar, Footer, Rightsidebar, RouterOutlet],
  templateUrl: './two-column.html',
  styles: ``
})
export class TwoColumn {

  private layout = inject(LayoutStore)

  isCondensed = false;

  ngOnInit(): void {
    window.addEventListener('resize', () => {
      if (this.layout.isTwoColumn()) {
        if (document.documentElement.clientWidth <= 767) {
          this.layout.setLayoutType('vertical')
          document.body.classList.add('twocolumn-panel');
        } else {
          this.layout.setLayoutType('twocolumn')
          document.body.classList.remove('twocolumn-panel');
          document.getElementById('side-bar')?.classList.add('d-none')
        }
      }
      else {
        if (document.body.classList.contains('twocolumn-panel')) {
          if (document.documentElement.clientWidth <= 767) {
            this.layout.setLayoutType('vertical')

          } else {
            this.layout.setLayoutType('twocolumn')
            document.body.classList.remove('twocolumn-panel')
            document.getElementById('side-bar')?.classList.add('d-none')
          }
        }
      }
    })
  }

  /**
   * On mobile toggle button clicked
   */
  onToggleMobileMenu() {
    if (document.documentElement.clientWidth <= 767) {
      document.body.classList.toggle('vertical-sidebar-enable');
      document.getElementById('side-bar')?.classList.remove('d-none')
    } else {
      document.body.classList.toggle('twocolumn-panel');
      document.getElementById('side-bar')?.classList.add('d-none')
    }
  }

  /**
   * on settings button clicked from topbar
   */
  onSettingsButtonClicked() {
    document.body.classList.toggle('right-bar-enabled');
    const rightBar = document.getElementById('theme-settings-offcanvas');
    if (rightBar != null) {
      rightBar.classList.toggle('show');
      rightBar.setAttribute('style', "visibility: visible;");
    }
  }

  isTwoColumnLayoutRequested() {
    return 'twocolumn' === document.documentElement.getAttribute('data-layout');

  }

  issemiboxLayoutRequested() {
    return 'semibox' === document.documentElement.getAttribute('data-layout');
  }

  onResize(event: any) {
    if (document.body.getAttribute('layout') == "twocolumn") {
      if (event.target.innerWidth <= 767) {
        this.layout.setLayoutType('vertical')
      } else {
        this.layout.setLayoutType('twocolumn');
        document.body.classList.remove('twocolumn-panel');
        document.body.classList.remove('vertical-sidebar-enable');
      }
    }
  }
}
