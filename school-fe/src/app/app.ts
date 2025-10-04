import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import lottie from "lottie-web";
import { defineElement } from "@lordicon/element";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styles: [],
})
export class App {
  protected title = 'school-fe';
  constructor(){
    defineElement()
  }
}
