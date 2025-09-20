import {Component, computed, inject, signal} from '@angular/core';
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
import {PermissionService} from "../../core/services/permission.service";
import {rxResource} from "@angular/core/rxjs-interop";
import {MusicService} from "../../core/api-services/music.service";

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

    private readonly permissionService = inject(PermissionService);
    private readonly musicSerivce = inject(MusicService);

    spotifyAuthenticated = rxResource({
        stream: () => this.musicSerivce.usageAllowed(),
        defaultValue: false
    })

    menuItems = computed<MenuItem[]>(() => {
        const spotifyAuthenticated = this.spotifyAuthenticated.value()
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
                label: 'music.navigation.library',
                customIcon: faMusic,
                routerLink: '/music/library',
                visible: this.permissionService.isAuthenticated(),
                items: [
                    {
                        label: 'music.navigation.artists',
                        routerLink: '/music/library/artists'
                    },
                    {
                        label: 'music.navigation.albums',
                        routerLink: '/music/library/albums'
                    },
                    {
                        label: 'music.navigation.tracks',
                        routerLink: '/music/library/tracks'
                    }
                ]
            },
            {
                label: 'music.navigation.spotify',
                customIcon: faSpotify,
                visible: spotifyAuthenticated,
                routerLink: '/music/spotify'
            },
            {
                label: 'music.navigation.settings',
                customIcon: faGears,
                routerLink: '/music/settings'
            }
        ] as MenuItem[]
    }

}
