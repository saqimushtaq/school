import { Component } from '@angular/core';
import { Topbar } from "../topbar/topbar";
import { HorizontalTopbar } from "../horizontal-topbar/horizontal-topbar";
import { Footer } from "../footer/footer";
import { Rightsidebar } from "../rightsidebar/rightsidebar";
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-horizontal',
  imports: [Topbar, HorizontalTopbar, Footer, Rightsidebar, RouterOutlet,],
  templateUrl: './horizontal.html',
  styles: ``
})
export class Horizontal {


  constructor() { }

  isCondensed = false;

  ngOnInit(): void {
  }

  /**
   * on settings button clicked from topbar
   */
   onSettingsButtonClicked() {
    document.body.classList.toggle('right-bar-enabled');
    const rightBar = document.getElementById('theme-settings-offcanvas');
    if(rightBar != null){
      rightBar.classList.toggle('show');
      rightBar.setAttribute('style',"visibility: visible;");
    }
  }

  /**
   * On mobile toggle button clicked
   */
   onToggleMobileMenu() {
   if (document.documentElement.clientWidth <= 1024) {
     document.body.classList.toggle('menu');
   }
 }

}
