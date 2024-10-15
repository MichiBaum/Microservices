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
    this.router.navigate(['/home'], {skipLocationChange: true});
  }

  github() {
    open('https://github.com/MichiBaum');
  }

  imprint() {
    this.router.navigate(['/imprint'], { skipLocationChange: true });
  }

  chess() {
    this.router.navigate(['/chess'], { skipLocationChange: true });
  }

  open(url: string) {
    window.open(url, '_blank');
  }

  microservices() {
    this.router.navigate(['/microservices'], { skipLocationChange: true })
  }
}
