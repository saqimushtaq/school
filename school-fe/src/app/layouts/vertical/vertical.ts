import { Component, inject } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { LayoutStore } from '../../stores/layout/layout.store';
import { Topbar } from "../topbar/topbar";
import { Sidebar } from "../sidebar/sidebar";
import { Footer } from "../footer/footer";
import { Rightsidebar } from "../rightsidebar/rightsidebar";

@Component({
  selector: 'app-vertical',
  imports: [Topbar, Sidebar, Footer, Rightsidebar, RouterOutlet],
  templateUrl: './vertical.html',
  styles: ``
})
export class Vertical {
  private router = inject(Router)
  private activatedRoute = inject(ActivatedRoute)

  layout = inject(LayoutStore)

  isCondensed = false;
  getsize: any;

  ngOnInit(): void {

    this.router.events.subscribe((event: any) => {
      if (document.documentElement.getAttribute('data-preloader') == 'enable') {
        if (event instanceof NavigationEnd) {
          // Update the attribute state based on the current route or any other conditions
          if (event.url !== '/disabled-route') {
            (document.getElementById("preloader") as HTMLElement).style.opacity = "1";
            (document.getElementById("preloader") as HTMLElement).style.visibility = "";
            setTimeout(() => {
              (document.getElementById("preloader") as HTMLElement).style.opacity = "0";
              (document.getElementById("preloader") as HTMLElement).style.visibility = "hidden";
            }, 1000);
          } else {
            (document.getElementById("preloader") as HTMLElement).style.opacity = "0";
            (document.getElementById("preloader") as HTMLElement).style.visibility = "hidden";
          }
        }
      }
    });

    this.handlePreloader(this.activatedRoute.snapshot.routeConfig?.path);
    if (document.documentElement.getAttribute('data-sidebar-size') == 'lg') {
      this.getsize = this.layout.sidebarSize()
      window.addEventListener('resize', () => {
        var self = this;
        if (document.documentElement.clientWidth <= 767) {
          document.documentElement.setAttribute('data-sidebar-size', '');
          document.querySelector('.hamburger-icon')?.classList.add('open')
        }
        else if (document.documentElement.clientWidth <= 1024) {
          document.documentElement.setAttribute('data-sidebar-size', 'sm');
          document.querySelector('.hamburger-icon')?.classList.add('open')
        }
        else if (document.documentElement.clientWidth >= 1024) {
          if (document.documentElement.getAttribute('data-layout-width') == 'fluid') {
            document.documentElement.setAttribute('data-sidebar-size', self.getsize);
            document.querySelector('.hamburger-icon')?.classList.remove('open')
          }
        }
      })
    }
  }
  private handlePreloader(route: any) {
    if (route !== '/disabled-route') {
      (document.getElementById("preloader") as HTMLElement).style.opacity = "1";
      (document.getElementById("preloader") as HTMLElement).style.visibility = "";
      setTimeout(() => {
        (document.getElementById("preloader") as HTMLElement).style.opacity = "0";
        (document.getElementById("preloader") as HTMLElement).style.visibility = "hidden";
      }, 1000);
    } else {
      (document.getElementById("preloader") as HTMLElement).style.opacity = "0";
      (document.getElementById("preloader") as HTMLElement).style.visibility = "hidden";
    }
  }


  /**
   * On mobile toggle button clicked
   */
  onToggleMobileMenu() {
    const currentSIdebarSize = document.documentElement.getAttribute("data-sidebar-size");
    if (document.documentElement.clientWidth >= 767) {
      if (currentSIdebarSize == null) {
        (document.documentElement.getAttribute('data-sidebar-size') == null || document.documentElement.getAttribute('data-sidebar-size') == "lg") ? document.documentElement.setAttribute('data-sidebar-size', 'sm') : document.documentElement.setAttribute('data-sidebar-size', 'lg')
      } else if (currentSIdebarSize == "md") {
        (document.documentElement.getAttribute('data-sidebar-size') == "md") ? document.documentElement.setAttribute('data-sidebar-size', 'sm') : document.documentElement.setAttribute('data-sidebar-size', 'md')
      } else {
        (document.documentElement.getAttribute('data-sidebar-size') == "sm") ? document.documentElement.setAttribute('data-sidebar-size', 'lg') : document.documentElement.setAttribute('data-sidebar-size', 'sm')
      }
    }

    if (document.documentElement.clientWidth <= 767) {
      document.body.classList.toggle('vertical-sidebar-enable');
    }
    this.isCondensed = !this.isCondensed;
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

  onResize(event: any) {
    if (document.body.getAttribute('layout') == "twocolumn") {
      if (event.target.innerWidth <= 767) {
       this.layout.setLayoutType('vertical')
      } else {
       this.layout.setLayoutType('twocolumn')
        document.body.classList.remove('twocolumn-panel');
        document.body.classList.remove('vertical-sidebar-enable');
      }
    }
  }
}
