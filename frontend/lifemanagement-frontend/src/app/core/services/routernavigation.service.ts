import {Injectable} from '@angular/core';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class RouternavigationService {

  constructor(private router: Router) { }

  homeNavigate() {
    this.router.navigate(['/home'], {skipLocationChange: true});
  }

  imprintNavigate() {
    this.router.navigate(['/imprint'], { skipLocationChange: true });
  }

  usersettingsNavigate() {
    this.router.navigate(['/usersettings'], { skipLocationChange: true });
  }

  logmanagementNavigate() {
    this.router.navigate(['/logmanagement'], { skipLocationChange: true });
  }

  loginNavigate() {
    this.router.navigate(['/login'], { skipLocationChange: true });
  }

}
