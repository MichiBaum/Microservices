import {Component, computed, inject, signal} from '@angular/core';
import {Menubar} from "primeng/menubar";
import {MenuItem} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-music-navigation',
    imports: [
        Menubar
    ],
  templateUrl: './music-navigation.component.html',
  styleUrl: './music-navigation.component.scss'
})
export class MusicNavigationComponent {
    private readonly translate = inject(TranslateService);

    spotifyAuthenticated = signal(true) // TODO

    menuItems = computed<MenuItem[]>(() => {
        const spotifyAuthenticated = this.spotifyAuthenticated()
        return this.createMenuItems(spotifyAuthenticated)
    })

    createMenuItems(spotifyAuthenticated: boolean): MenuItem[]{
        return [
            {
                label: this.translate.instant('music.navigation.home'),
                icon: 'pi pi-home',
                visible: true,
                routerLink: '/music/home'
            },
            {
                label: this.translate.instant('music.navigation.settings.spotify-login'),
                icon: 'pi pi-newspaper',
                visible: spotifyAuthenticated,
                routerLink: '/music/settings/spotify-login'
            }
        ] as MenuItem[]
    }

}
