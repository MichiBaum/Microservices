import {ActivatedRouteSnapshot, Routes} from '@angular/router';
import {HomeComponent} from "./home/home.component";
import {Sides} from "./core/config/sides";
import {Permissions} from "./core/config/permissions";
import {isAuthenticatedGuard, isPermittedGuard} from "./core/guards/auth.guard";
import {titleResolver} from "./core/route-resolver/title-resolver";
import {HeaderService} from "./core/services/header.service";
import {TranslateService} from "@ngx-translate/core";
import {chessEventRouteResolver} from "./core/route-resolver/chess-event.route-resolver";
import {ChessEvent} from "./core/models/chess/chess.models";


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
        title: titleResolver,
        data: {
            "tabTitle": (translate: TranslateService) => translate.get("home.tab-title"),
            "headerTitle": (headerService: HeaderService) => headerService.changeTitle("home.title"),
            "metaDescription": (translate: TranslateService) => translate.get("home.meta-description")
        }
    },
    {
        path: 'home',
        component: HomeComponent,
        canActivate: [],
        title: titleResolver,
        data: {
            "tabTitle": (translate: TranslateService) => translate.get("home.tab-title"),
            "headerTitle": (headerService: HeaderService) => headerService.changeTitle("home.title"),
            "metaDescription": (translate: TranslateService) => translate.get("home.meta-description")
        }
    },
    {
        path: 'login',
        loadComponent: () => import("./login/login.component").then((c) => c.LoginComponent),
        canActivate: [],
        title: titleResolver,
        data: {
            "tabTitle": (translate: TranslateService) => translate.get("login.tab-title"),
            "headerTitle": (headerService: HeaderService) => headerService.changeTitle("login.title"),
            "metaDescription": (translate: TranslateService) => translate.get("login.meta-description")
        }
    },
    {
        path: 'register',
        loadComponent: () => import("./register/register.component").then((c) => c.RegisterComponent),
        canActivate: [],
        title: titleResolver,
        data: {
            "tabTitle": (translate: TranslateService) => translate.get("register.tab-title"),
            "headerTitle": (headerService: HeaderService) => headerService.changeTitle("register.title"),
            "metaDescription": (translate: TranslateService) => translate.get("register.meta-description")
        }
    },
    {
        path: 'imprint',
        loadComponent: () => import("./imprint/imprint.component").then((c) => c.ImprintComponent),
        canActivate: [],
        title: titleResolver,
        data: {
            "tabTitle": (translate: TranslateService) => translate.get("imprint.tab-title"),
            "headerTitle": (headerService: HeaderService) => headerService.changeTitle("imprint.title"),
            "metaDescription": (translate: TranslateService) => translate.get("imprint.meta-description")
        }
    },
    {
        path: "about-me",
        loadComponent: () => import("./about-me/about-me.component").then((c) => c.AboutMeComponent),
        canActivate: [],
        title: titleResolver,
        data: {
            "tabTitle": (translate: TranslateService) => translate.get("about-me.title-with-name"),
            "headerTitle": (headerService: HeaderService) => headerService.changeTitle("about-me.title-with-name"),
            "metaDescription": (translate: TranslateService) => translate.get("about-me.meta-description")
        }
    },
    {
        path: 'donate',
        loadComponent: () => import("./donate/donate.component").then((c) => c.DonateComponent),
        canActivate: [],
        title: titleResolver,
        data: {
            "tabTitle": (translate: TranslateService) => translate.get("donate.tab-title"),
            "headerTitle": (headerService: HeaderService) => headerService.changeTitle("donate.title"),
            "metaDescription": (translate: TranslateService) => translate.get("donate.meta-description")
        }
    },
    {
        path: 'microservices',
        loadComponent: () => import("./microservice-overview/microservice-overview.component").then((c) => c.MicroserviceOverviewComponent),
        canActivate: [isAuthenticatedGuard, isPermittedGuard],
        title: titleResolver,
        data: {
            "tabTitle": (translate: TranslateService) => translate.get("microservices.tab-title"),
            "headerTitle": (headerService: HeaderService) => headerService.changeTitle("microservices.title"),
            "metaDescription": (translate: TranslateService) => translate.get("microservices.meta-description"),
            "permissions": [Permissions.ADMIN_SERVICE]
        }
    },
    {
        path: "chess",
        loadComponent: () => import("./chess/chess.component").then((c) => c.ChessComponent),
        children: [
            {
                path: "",
                pathMatch: "full",
                loadComponent: () => import("./chess/chess-home/chess-home.component").then((c) => c.ChessHomeComponent),
                title: titleResolver,
                data: {
                    "tabTitle": (translate: TranslateService) => translate.get("chess.tab-title"),
                    "headerTitle": (headerService: HeaderService) => headerService.changeTitle("chess.title"),
                    "metaDescription": (translate: TranslateService) => translate.get("chess.meta-description")
                },
            },
            {
                path: "news",
                loadComponent: () => import("./chess/chess-news/chess-news.component").then((c) => c.ChessNewsComponent),
                title: titleResolver,
                data: {
                    "tabTitle": (translate: TranslateService) => translate.get("chess.news.tab-title"),
                    "headerTitle": (headerService: HeaderService) => headerService.changeTitle("chess.news.title"),
                    "metaDescription": (translate: TranslateService) => translate.get("chess.news.meta-description")
                },
            },
            {
                path: "events",
                loadComponent: () => import("./chess/chess-events-list/chess-events-list.component").then((c) => c.ChessEventsListComponent),
                title: titleResolver,
                data: {
                    "tabTitle": (translate: TranslateService) => translate.get("chess.events.tab-title"),
                    "headerTitle": (headerService: HeaderService) => headerService.changeTitle("chess.events.title"),
                    "metaDescription": (translate: TranslateService) => translate.get("chess.events.meta-description")
                },
            },
            {
                path: "events/:id",
                resolve: {
                    event: chessEventRouteResolver
                },
                children: [
                    {
                        path: "",
                        pathMatch: "full",
                        loadComponent: () => import("./chess/chess-event/chess-event.component").then((c) => c.ChessEventComponent),
                        title: titleResolver,
                        data: {
                            "tabTitle": (translate: TranslateService, route: ActivatedRouteSnapshot) =>
                                translate.get("chess.event.tab-title", {title: route?.parent?.data?.['event']?.title}),
                            "headerTitle": (headerService: HeaderService, route: ActivatedRouteSnapshot) =>
                                headerService.changeTitle("chess.event.title", {title: route?.parent?.data?.['event']?.title}),
                            "metaDescription": (translate: TranslateService, route: ActivatedRouteSnapshot) => {
                                const event = route?.parent?.data?.['event'] as ChessEvent | undefined;
                                return translate.get(
                                    "chess.event.meta-description",
                                    {
                                        title: event?.title,
                                        from: event?.dateFrom,
                                        to: event?.dateTo,
                                        location: event?.location
                                    })
                            }
                        },
                    }
                ]
            },
            {
                path: "openings/:id",
                loadComponent: async () => {
                    if (window.innerWidth > 1000) {
                        const c = await import("./chess/chess-openings/chess-opening/chess-opening.component");
                        return c.ChessOpeningComponent;
                    } else {
                        const c = await import("./chess/chess-openings/chess-opening-mobile/chess-opening-mobile.component");
                        return c.ChessOpeningMobileComponent;
                    }
                },
                title: titleResolver,
                data: {
                    "tabTitle": (translate: TranslateService) => translate.get("chess.openings.tab-title"),
                    "headerTitle": (headerService: HeaderService) => headerService.changeTitle("chess.openings.title"),
                    "metaDescription": (translate: TranslateService) => translate.get("chess.openings.meta-description")
                },
            },
            {
                path: "settings",
                canActivate: [isAuthenticatedGuard, isPermittedGuard],
                data: {"permissions": [Permissions.CHESS_SERVICE_ADMIN]},
                children: [
                    {
                        path: "persons",
                        loadComponent: () => import("./chess-settings/chess-update-person/chess-update-person.component").then((c) => c.ChessUpdatePersonComponent),
                        canActivate: [isAuthenticatedGuard, isPermittedGuard],
                        data: {"permissions": [Permissions.CHESS_SERVICE_ADMIN]},
                    },
                    {
                        path: "accounts",
                        loadComponent: () => import("./chess-settings/chess-update-account/chess-update-account.component").then((c) => c.ChessUpdateAccountComponent),
                        canActivate: [isAuthenticatedGuard, isPermittedGuard],
                        data: {"permissions": [Permissions.CHESS_SERVICE_ADMIN]},
                    },
                    {
                        path: "event-categories",
                        loadComponent: () => import("./chess-settings/chess-update-event-category/chess-update-event-category.component").then((c) => c.ChessUpdateEventCategoryComponent),
                        canActivate: [isAuthenticatedGuard, isPermittedGuard],
                        data: {"permissions": [Permissions.CHESS_SERVICE_ADMIN]},
                    },
                    {
                        path: "events",
                        loadComponent: () => import("./chess-settings/chess-update-event/chess-update-event.component").then((c) => c.ChessUpdateEventComponent),
                        canActivate: [isAuthenticatedGuard, isPermittedGuard],
                        data: {"permissions": [Permissions.CHESS_SERVICE_ADMIN]},
                    },
                    {
                        path: "games",
                        loadComponent: () => import("./chess-settings/chess-update-game/chess-update-game.component").then((c) => c.ChessUpdateGameComponent),
                        canActivate: [isAuthenticatedGuard, isPermittedGuard],
                        data: {"permissions": [Permissions.CHESS_SERVICE_ADMIN]},
                    },
                    {
                        path: "fide-import",
                        loadComponent: () => import("./chess-settings/fide-import/fide-import.component").then((c) => c.FideImportComponent),
                        canActivate: [isAuthenticatedGuard, isPermittedGuard],
                        data: {"permissions": [Permissions.CHESS_SERVICE_ADMIN]},
                    },
                    {
                        path: "engines",
                        loadComponent: () => import("./chess-settings/chess-update-engine/chess-update-engine.component").then((c) => c.ChessUpdateEngineComponent),
                        canActivate: [isAuthenticatedGuard, isPermittedGuard],
                        data: {"permissions": [Permissions.CHESS_SERVICE_ADMIN]},
                    },
                    {
                        path: "openings",
                        loadComponent: () => import("./chess-settings/chess-update-opening/chess-update-opening.component").then((c) => c.ChessUpdateOpeningComponent),
                        canActivate: [isAuthenticatedGuard, isPermittedGuard],
                        data: {"permissions": [Permissions.CHESS_SERVICE_ADMIN]},
                    }
                ]
            }
        ]
    },
    {
        path: 'fitness',
        loadComponent: () => import("./fitness/fitness.component").then((c) => c.FitnessComponent),
        canActivate: [isAuthenticatedGuard, isPermittedGuard],
        data: {"permissions": [Permissions.FITNESS_SERVICE]}
    },
    {
        path: 'notes',
        canActivate: [isAuthenticatedGuard],
        children: [
            {
                path: '',
                loadComponent: () => import("./note/notes.component").then((c) => c.NotesComponent),
                canActivate: [isAuthenticatedGuard],
            },
            {
                path: "edit/:id",
                loadComponent: () => import("./note/edit-note/edit-note.component").then((c) => c.EditNoteComponent),
                canActivate: [isAuthenticatedGuard]
            }
        ]
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
