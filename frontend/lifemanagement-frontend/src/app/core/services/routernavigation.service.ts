import {Injectable} from '@angular/core';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class RouternavigationService {

  constructor(private router: Router) { }

  homeNavigate() {
    this.router.navigate(['/home'], {skipLocationChange: true}).then();
  }

  imprintNavigate() {
    this.router.navigate(['/imprint'], { skipLocationChange: true }).then();
  }

  usersettingsNavigate() {
    this.router.navigate(['/usersettings'], { skipLocationChange: true }).then();
  }

  logmanagementNavigate() {
    this.router.navigate(['/logmanagement'], { skipLocationChange: true }).then();
  }

  loginNavigate() {
    this.router.navigate(['/login'], { skipLocationChange: true }).then();
  }

}
