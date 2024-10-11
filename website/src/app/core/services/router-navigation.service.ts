import {Router} from '@angular/router';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RouternavigationService {

  constructor(private router: Router) {
  }

  homeNavigate() {
    this.router.navigate(['/home'], {skipLocationChange: true});
  }

  githubNavigate() {
    const url = 'https://github.com/MichiBaum';
    window.open(url, '_blank');
  }

  imprintNavigate() {
    this.router.navigate(['/imprint'], { skipLocationChange: true });
  }

  conceptNavigate() {

  }

  chessNavigation() {
    this.router.navigate(['/chess'], { skipLocationChange: true });
  }
}
