import {Router} from '@angular/router';
import {inject, Injectable} from '@angular/core';
import {EnvironmentConfig} from "../config/environment.config";

@Injectable({
    providedIn: 'root'
})
export class RouterNavigationService {
    private router = inject(Router);
    private environment = inject(EnvironmentConfig)


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

    openPopup(url: string) {
        const handle = window.open(url, '_blank', 'popup');
        if (!handle) {
            this.open(url)
        }
    }

    microservices() {
        this.router.navigate(['/microservices'])
    }

    zipkin() {
        open(this.environment.zipkin())
    }

    admin() {
        open(this.environment.adminService())
    }

    grafana() {
        open(this.environment.grafana())
    }

    prometheus() {
        open(this.environment.prometheus())
    }

    login() {
        this.router.navigate(['/login']);
    }

    about_me() {
        this.router.navigate(['/about-me']);
    }

    donate() {
        this.router.navigate(['/donate']);
    }

    buymeacoffee() {
        open('https://www.buymeacoffee.com/michibaum');
    }

    linkedIn() {
        open('https://linkedin.com/in/michael-baumberger-a06306198/')
    }

    hosttech() {
        open('https://www.hosttech.ch/?promocode=31886957')
    }

    register() {
        this.router.navigate(['/register']);
    }

    fitness() {
        this.router.navigate(['/fitness']);
    }

    music() {
        this.router.navigate(['/music']);
    }

    notes() {
        this.router.navigate(['/notes'])
    }

    donateGithub() {
        open('https://github.com/sponsors/MichiBaum')
    }
}
