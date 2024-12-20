import {Routes} from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {HomeComponent} from "./home/home.component";
import {Sides} from "./core/config/sides";
import {RegisterComponent} from "./register/register.component";
import {Permissions} from "./core/config/permissions";
import {isAuthenticatedGuard, isPermittedGuard} from "./core/guards/auth.guard";

/**
 * Defines the application routes and their respective components, guards, and additional configuration.
 * The routes are organized by their `path`, which determines the URL segment for navigation.
 * Components are either directly loaded or lazy-loaded using dynamic imports, with optional guard conditions applied.
 * Certain routes include nested child routes or provide additional metadata for permissions.
 */
export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    canActivate: [],
  },
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [],
  },
  {
    path: 'login',
    loadComponent: () => import("./login/login.component").then((c) => c.LoginComponent),
    canActivate: [],
  },
  {
    path: 'register',
    loadComponent: () => import("./register/register.component").then((c) => c.RegisterComponent),
    canActivate: [],
  },
  {
    path: 'microservices',
    loadComponent: () => import("./microservice-overview/microservice-overview.component").then((c) => c.MicroserviceOverviewComponent),
    canActivate: [isAuthenticatedGuard, isPermittedGuard],
    data: {"permissions": [Permissions.ADMIN_SERVICE]}
  },
  {
    path: 'imprint',
    loadComponent: () => import("./imprint/imprint.component").then((c) => c.ImprintComponent),
    canActivate: [],
  },
  {
    path: "about-me",
    loadComponent: () => import("./about-me/about-me.component").then((c) => c.AboutMeComponent),
    canActivate: [],
  },
  {
    path: "chess",
    loadComponent: () => import("./chess/chess.component").then((c) => c.ChessComponent),
    children: [
      {
        path: "news",
        loadComponent: () => import("./chess/chess-news/chess-news.component").then((c) => c.ChessNewsComponent),
        canActivate: []
      },
      {
        path: "events",
        loadComponent: () => import("./chess/chess-events-list/chess-events-list.component").then((c) => c.ChessEventsListComponent),
        canActivate: []
      },
      {
        path: "events/:id",
        loadComponent: () => import("./chess/chess-event/chess-event.component").then((c) => c.ChessEventComponent),
        canActivate: []
      },
      {
        path: "player-analysis",
        loadComponent: () => import("./chess/chess-player-analysis/chess-player-analysis.component").then((c) => c.ChessPlayerAnalysisComponent),
        canActivate: [isAuthenticatedGuard, isPermittedGuard],
        data: {"permissions": [Permissions.CHESS_SERVICE]},
      },
      {
        path: "settings",
        canActivate: [isAuthenticatedGuard, isPermittedGuard],
        data: {"permissions": [Permissions.CHESS_SERVICE_ADMIN]},
        children: [
          {
            path:"persons",
            loadComponent: () => import("./chess-settings/chess-update-person/chess-update-person.component").then((c) => c.ChessUpdatePersonComponent),
            canActivate: [isAuthenticatedGuard, isPermittedGuard],
            data: {"permissions": [Permissions.CHESS_SERVICE_ADMIN]},
          },
          {
            path:"accounts",
            loadComponent: () => import("./chess-settings/chess-update-account/chess-update-account.component").then((c) => c.ChessUpdateAccountComponent),
            canActivate: [isAuthenticatedGuard, isPermittedGuard],
            data: {"permissions": [Permissions.CHESS_SERVICE_ADMIN]},
          },
          {
            path:"event-categories",
            loadComponent: () => import("./chess-settings/chess-update-event-category/chess-update-event-category.component").then((c) => c.ChessUpdateEventCategoryComponent),
            canActivate: [isAuthenticatedGuard, isPermittedGuard],
            data: {"permissions": [Permissions.CHESS_SERVICE_ADMIN]},
          },
          {
            path:"events",
            loadComponent: () => import("./chess-settings/chess-update-event/chess-update-event.component").then((c) => c.ChessUpdateEventComponent),
            canActivate: [isAuthenticatedGuard, isPermittedGuard],
            data: {"permissions": [Permissions.CHESS_SERVICE_ADMIN]},
          },
          {
            path:"games",
            loadComponent: () => import("./chess-settings/chess-update-game/chess-update-game.component").then((c) => c.ChessUpdateGameComponent),
            canActivate: [isAuthenticatedGuard, isPermittedGuard],
            data: {"permissions": [Permissions.CHESS_SERVICE_ADMIN]},
          }
        ]
      }
    ]
  },
  {
    path: 'donate',
    loadComponent: () => import("./donate/donate.component").then((c) => c.DonateComponent),
    canActivate: [],
  },
  {
    path: 'fitness',
    loadComponent: () => import("./fitness/fitness.component").then((c) => c.FitnessComponent),
    canActivate: [isAuthenticatedGuard, isPermittedGuard],
    data: {"permissions": [Permissions.FITNESS_SERVICE]}
  },
  {
    path: Sides.fitness_settings.navigation, // TODO remove like chess settings
    loadComponent: () => import("./fitness-settings/fitness-settings.component").then((c) => c.FitnessSettingsComponent),
    canActivate: [isAuthenticatedGuard, isPermittedGuard],
    data: {"permissions": [Permissions.FITNESS_SERVICE]}
  },
  {
    path: 'music',
    loadComponent: () => import("./music/music.component").then((c) => c.MusicComponent),
    canActivate: [isAuthenticatedGuard, isPermittedGuard],
    data: {"permissions": [Permissions.MUSIC_SERVICE]}
  },
  {
    path: Sides.music_settings.navigation, // TODO remove like chess settings
    loadComponent: () => import("./music-settings/music-settings.component").then((c) => c.MusicSettingsComponent),
    canActivate: [isAuthenticatedGuard, isPermittedGuard],
    data: {"permissions": [Permissions.MUSIC_SERVICE]}
  }
];
