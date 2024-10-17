import {Router} from '@angular/router';
import {Injectable} from '@angular/core';
import {Sides} from "../config/sides";

@Injectable({
  providedIn: 'root'
})
export class RouternavigationService {

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
}
