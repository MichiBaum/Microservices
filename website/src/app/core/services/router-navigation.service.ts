import {Router} from '@angular/router';
import {Injectable} from '@angular/core';
import {Sides} from "../config/sides";

@Injectable({
  providedIn: 'root'
})
export class RouterNavigationService {

  constructor(private router: Router) {
  }

  home() {
    this.router.navigate(['/home']);
  }

  github() {
    open('https://github.com/MichiBaum');
  }

  imprint() {
    this.router.navigate(['/imprint']);
  }

  chess() {
    this.router.navigate(['/chess']);
  }

  open(url: string) {
    window.open(url, '_blank');
  }

  microservices() {
    this.router.navigate(['/microservices'])
  }

  login() {
    this.router.navigate(['/login']);
  }

  about_me(){
    this.router.navigate(['/about-me']);
  }

  donate() {
    this.router.navigate(['/donate']);
    // open('https://www.buymeacoffee.com/michibaum');
  }

  buymeacoffee() {
    open('https://www.buymeacoffee.com/michibaum');
  }

  linkedIn() {
    open('https://linkedin.com/in/michael-baumberger-a06306198/')
  }

  hosttech(){
    open('https://www.hosttech.ch/?promocode=31886957')
  }

  register() {
    this.router.navigate(['/register']);
  }

  fitness() {
    this.router.navigate(['/fitness']);
  }
}
