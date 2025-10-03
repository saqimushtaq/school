import { Component, EventEmitter, inject, Output, TemplateRef, ViewChild } from '@angular/core';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { LayoutStore } from '../../stores/layout/layout.store';
import { BackgroundImage, LayoutMode, LayoutPositionType, LayoutTheme, LayoutThemeColor, LayoutTopbarColor, LayoutType, LayoutWidthType, PreloaderType, SidebarColor, SidebarImage, SidebarSize, SidebarView, SidebarVisibility } from '../../stores/layout/layout-types';
import { SimplebarAngularModule } from 'simplebar-angular';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-rightsidebar',
  imports: [SimplebarAngularModule, CommonModule],
  templateUrl: './rightsidebar.html',
  styles: ``
})
export class Rightsidebar {

  private offcanvasService = inject(NgbOffcanvas)
   store = inject(LayoutStore)

  layout: string | undefined;
  theme: string | undefined;
  themecolor: string | undefined;
  mode: string | undefined;
  width: string | undefined;
  position: string | undefined;
  topbar: string | undefined;
  size: string | undefined;
  sidebarView: string | undefined;
  sidebar: string | undefined;
  attribute: LayoutType = 'vertical';
  sidebarImage: any;
  sidebarVisibility: any;
  preLoader: any;
  grd: any;
  backgroundImage: any

  @ViewChild('filtetcontent') filtetcontent!: TemplateRef<any>;
  @Output() settingsButtonClicked = new EventEmitter();


  ngOnInit(): void {
    setTimeout(() => {
      if (this.offcanvasService.hasOpenOffcanvas() == false) {
        this.openEnd(this.filtetcontent);
      };
    }, 1000);

      this.layout = this.store.layoutType();
      this.theme = this.store.layoutTheme();
      this.themecolor = this.store.layoutThemeColor();
      this.mode = this.store.layoutMode();
      this.width = this.store.layoutWidth();
      this.position = this.store.layoutPosition();
      this.topbar = this.store.topbarColor();
      this.size = this.store.sidebarSize();
      this.sidebarView = this.store.sidebarView();
      this.sidebar = this.store.sidebarColor();
      this.sidebarImage = this.store.sidebarImage();
      this.preLoader = this.store.preloader();
      this.sidebarVisibility = this.store.sidebarVisibility()
      this.attribute = this.store.layoutType()

  }

  /**
   * Change the layout onclick
   * @param layout Change the layout
   */
  changeLayout(layout: LayoutType) {
    this.attribute = layout;
    this.store.setLayoutType(layout)
    setTimeout(() => {
      window.dispatchEvent(new Event('resize'));
    }, 100);
  }


  // Add Active Class
  addActive(grdSidebar: SidebarColor) {
    this.grd = grdSidebar;
    document.documentElement.setAttribute('data-sidebar', grdSidebar)
    document.getElementById('collapseBgGradient')?.classList.toggle('show');
    document.getElementById('collapseBgGradient1')?.classList.add('active');
  }

  // Remove Active Class
  removeActive() {
    this.grd = '';
    document.getElementById('collapseBgGradient1')?.classList.remove('active');
    document.getElementById('collapseBgGradient')?.classList.remove('show');
  }

  // When the user clicks on the button, scroll to the top of the document
  topFunction() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
  }

  //  Filter Offcanvas Set
  openEnd(content: TemplateRef<any>) {
    this.offcanvasService.open(content, { position: 'end' });

    setTimeout(() => {
      this.attribute = document.documentElement.getAttribute('data-layout') as LayoutType
      if (this.attribute == 'vertical') {
        var vertical = document.getElementById('customizer-layout01') as HTMLInputElement;
        if (vertical != null) {
          vertical.setAttribute('checked', 'true');
        }
      }
      if (this.attribute == 'horizontal') {
        const horizontal = document.getElementById('customizer-layout02');
        if (horizontal != null) {
          horizontal.setAttribute('checked', 'true');
        }
      }
      if (this.attribute == 'twocolumn') {
        const Twocolumn = document.getElementById('customizer-layout03');
        if (Twocolumn != null) {
          Twocolumn.setAttribute('checked', 'true');
        }
      }
      if (this.attribute == 'semibox') {
        const Twocolumn = document.getElementById('customizer-layout04');
        if (Twocolumn != null) {
          Twocolumn.setAttribute('checked', 'true');
        }
      }
    }, 100);
  }

  // Show Profile Sidebar
  showSidebarProgile(event: any) {
    if (event.target.checked == true) {
      document.documentElement.setAttribute("data-sidebar-user-show", "")
    } else {
      document.documentElement.removeAttribute("data-sidebar-user-show")
    }
  }

  // Theme Change
  changelayoutTheme(theme: LayoutTheme) {
    this.theme = theme;
    this.store.setLayoutTheme(theme)


    if (theme == 'saas') {
      const layout = 'horizontal';
      this.store.setLayoutType(layout)
    } else if (theme == 'creative') {
      const layout = 'twocolumn';
      this.store.setLayoutType(layout)
    } else {
      const layout = 'vertical';
      this.store.setLayoutType(layout)
    }

    if (theme == 'galaxy') {
      this.store.setLayoutMode('dark')
    } else {
      this.store.setLayoutMode('light')
    }

    if (theme == 'modern') {
      this.store.setSidebarSize('sm-hover')
    } else {
      this.store.setSidebarSize('sm')
    }

    setTimeout(() => {
      window.dispatchEvent(new Event('resize'));
    }, 100);
  }

  // Change Theme Color
  changeColor(themecolor: LayoutThemeColor) {
    this.themecolor = themecolor;
    this.store.setLayoutThemeColor( themecolor );

  }

  // Mode Change
  changeLayoutMode(mode: LayoutMode) {
    this.mode = mode;
    this.store.setLayoutMode(mode);

  }

  // Visibility Change
  changeVisibility(sidebarvisibility: SidebarVisibility) {
    this.sidebarVisibility = sidebarvisibility;
    this.store.setSidebarVisibility(sidebarvisibility);

  }

  // Width Change
  changeWidth(layoutWidth: LayoutWidthType, size: SidebarSize) {
    this.width = layoutWidth;
    this.store.setLayoutWidth(layoutWidth)
    this.store.setSidebarSize(size)

    setTimeout(() => {
      window.dispatchEvent(new Event('resize'));
    }, 100);
  }

  // Position Change
  changePosition(layoutPosition: LayoutPositionType) {
    this.position = layoutPosition;
    this.store.setLayoutPosition(layoutPosition)

  }

  // Topbar Change
  changeTopColor(topbarColor: LayoutTopbarColor) {
    this.topbar = topbarColor;
    this.store.setTopbarColor(topbarColor)
  }

  // Sidebar Size Change
  changeSidebarSize(sidebarSize: SidebarSize) {
    this.size = sidebarSize;
   this.store.setSidebarSize(sidebarSize)
  }

  // Sidebar Size Change
  changeSidebar(sidebarView: SidebarView) {
    this.sidebarView = sidebarView;
    this.store.setSidebarView(sidebarView)
  }

  // Sidebar Color Change
  changeSidebarColor(sidebarColor: SidebarColor) {
    this.sidebar = sidebarColor;
    this.store.setSidebarColor(sidebarColor)
  }

  // Sidebar Image Change
  changeSidebarImage(sidebarImage: SidebarImage) {
    this.sidebarImage = sidebarImage;
    this.store.setSidebarImage(sidebarImage)
  }

  // Sidebar Image Change
  changeBackgroundImage(backgroundImage: BackgroundImage) {
    this.backgroundImage = backgroundImage;
    this.store.setBackgroundImage(backgroundImage)
  }

  // PreLoader Image Change
  changeLoader(Preloader: PreloaderType) {
    this.preLoader = Preloader;
    this.store.setPreloader(Preloader);


    var preloader = document.getElementById("preloader");
    if (preloader) {
      setTimeout(function () {
        (document.getElementById("preloader") as HTMLElement).style.opacity = "0";
        (document.getElementById("preloader") as HTMLElement).style.visibility = "hidden";
      }, 1000);
    }
  }
}
