import {Component, computed, signal} from '@angular/core';
import {Menubar} from "primeng/menubar";
import {MenuItem} from "primeng/api";
import {TranslatePipe} from "@ngx-translate/core";
import {Badge} from "primeng/badge";
import {Divider} from "primeng/divider";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {NgClass, NgIf} from "@angular/common";
import {Ripple} from "primeng/ripple";
import {Tag} from "primeng/tag";
import {faAngleDown, faGears, faHouse, faMusic} from "@fortawesome/free-solid-svg-icons";
import {RouterLink} from "@angular/router";
import {faSpotify} from "@fortawesome/free-brands-svg-icons";

@Component({
  selector: 'app-music-navigation',
    imports: [
        Menubar,
        Badge,
        Divider,
        FaIconComponent,
        NgIf,
        Ripple,
        Tag,
        TranslatePipe,
        RouterLink,
        NgClass
    ],
  templateUrl: './music-navigation.component.html',
  styleUrl: './music-navigation.component.scss'
})
export class MusicNavigationComponent {
    protected readonly faAngleDown = faAngleDown;
    protected readonly faMusic = faMusic;

    spotifyAuthenticated = signal(true) // TODO

    menuItems = computed<MenuItem[]>(() => {
        const spotifyAuthenticated = this.spotifyAuthenticated()
        return this.createMenuItems(spotifyAuthenticated)
    })

    createMenuItems(spotifyAuthenticated: boolean): MenuItem[]{
        return [
            {
                label: 'music.navigation.home',
                customIcon: faHouse,
                routerLink: '/music'
            },
            {
                label: 'music.navigation.spotify',
                customIcon: faSpotify,
                visible: spotifyAuthenticated,
                routerLink: '/music/spotify'
            },
            {
                label: 'chess.navigation.settings',
                customIcon: faGears,
                routerLink: '/music/settings'
            }
        ] as MenuItem[]
    }

}
