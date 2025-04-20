import {Component, inject} from '@angular/core';
import {AuthService} from "../../core/api-services/auth.service";
import {Button} from "primeng/button";

@Component({
  selector: 'app-cookie-admin',
    imports: [
        Button
    ],
  templateUrl: './cookie-admin.component.html',
  styleUrl: './cookie-admin.component.css'
})
export class CookieAdminComponent {
    authService: AuthService = inject(AuthService);

    setCookie() {
        this.authService.setJwtAsCookie()
    }

    removeCookie() {
        this.authService.removeAuthCookie()
    }
}


