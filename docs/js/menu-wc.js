'use strict';


customElements.define('compodoc-menu', class extends HTMLElement {
    constructor() {
        super();
        this.isNormalMode = this.getAttribute('mode') === 'normal';
    }

    connectedCallback() {
        this.render(this.isNormalMode);
    }

    render(isNormalMode) {
        let tp = lithtml.html(`
        <nav>
            <ul class="list">
                <li class="title">
                    <a href="index.html" data-type="index-link">
                        <img alt="" class="img-responsive" data-type="compodoc-logo" data-src=images/favicon-152x152.png> 
                    </a>
                </li>

                <li class="divider"></li>
                ${ isNormalMode ? `<div id="book-search-input" role="search"><input type="text" placeholder="Type to search"></div>` : '' }
                <li class="chapter">
                    <a data-type="chapter-link" href="index.html"><span class="icon ion-ios-home"></span>Getting started</a>
                    <ul class="links">
                        <li class="link">
                            <a href="index.html" data-type="chapter-link">
                                <span class="icon ion-ios-keypad"></span>Overview
                            </a>
                        </li>
                                <li class="link">
                                    <a href="dependencies.html" data-type="chapter-link">
                                        <span class="icon ion-ios-list"></span>Dependencies
                                    </a>
                                </li>
                    </ul>
                </li>
                    <li class="chapter modules">
                        <a data-type="chapter-link" href="modules.html">
                            <div class="menu-toggler linked" data-toggle="collapse" ${ isNormalMode ?
                                'data-target="#modules-links"' : 'data-target="#xs-modules-links"' }>
                                <span class="icon ion-ios-archive"></span>
                                <span class="link-name">Modules</span>
                                <span class="icon ion-ios-arrow-down"></span>
                            </div>
                        </a>
                        <ul class="links collapse " ${ isNormalMode ? 'id="modules-links"' : 'id="xs-modules-links"' }>
                            <li class="link">
                                <a href="modules/AppModule.html" data-type="entity-link">AppModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-AppModule-9d13559292bbb79eb14efa788542e2e9"' : 'data-target="#xs-components-links-module-AppModule-9d13559292bbb79eb14efa788542e2e9"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-AppModule-9d13559292bbb79eb14efa788542e2e9"' :
                                            'id="xs-components-links-module-AppModule-9d13559292bbb79eb14efa788542e2e9"' }>
                                            <li class="link">
                                                <a href="components/AppComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">AppComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                                <li class="chapter inner">
                                    <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                        'data-target="#injectables-links-module-AppModule-9d13559292bbb79eb14efa788542e2e9"' : 'data-target="#xs-injectables-links-module-AppModule-9d13559292bbb79eb14efa788542e2e9"' }>
                                        <span class="icon ion-md-arrow-round-down"></span>
                                        <span>Injectables</span>
                                        <span class="icon ion-ios-arrow-down"></span>
                                    </div>
                                    <ul class="links collapse" ${ isNormalMode ? 'id="injectables-links-module-AppModule-9d13559292bbb79eb14efa788542e2e9"' :
                                        'id="xs-injectables-links-module-AppModule-9d13559292bbb79eb14efa788542e2e9"' }>
                                        <li class="link">
                                            <a href="injectables/LanguageConfig.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>LanguageConfig</a>
                                        </li>
                                    </ul>
                                </li>
                            </li>
                            <li class="link">
                                <a href="modules/AppRoutingModule.html" data-type="entity-link">AppRoutingModule</a>
                            </li>
                            <li class="link">
                                <a href="modules/CoreModule.html" data-type="entity-link">CoreModule</a>
                                <li class="chapter inner">
                                    <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                        'data-target="#directives-links-module-CoreModule-f64cab7352eb4a3af4df788eb6ba6f67"' : 'data-target="#xs-directives-links-module-CoreModule-f64cab7352eb4a3af4df788eb6ba6f67"' }>
                                        <span class="icon ion-md-code-working"></span>
                                        <span>Directives</span>
                                        <span class="icon ion-ios-arrow-down"></span>
                                    </div>
                                    <ul class="links collapse" ${ isNormalMode ? 'id="directives-links-module-CoreModule-f64cab7352eb4a3af4df788eb6ba6f67"' :
                                        'id="xs-directives-links-module-CoreModule-f64cab7352eb4a3af4df788eb6ba6f67"' }>
                                        <li class="link">
                                            <a href="directives/SecuredDirective.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules">SecuredDirective</a>
                                        </li>
                                    </ul>
                                </li>
                                <li class="chapter inner">
                                    <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                        'data-target="#injectables-links-module-CoreModule-f64cab7352eb4a3af4df788eb6ba6f67"' : 'data-target="#xs-injectables-links-module-CoreModule-f64cab7352eb4a3af4df788eb6ba6f67"' }>
                                        <span class="icon ion-md-arrow-round-down"></span>
                                        <span>Injectables</span>
                                        <span class="icon ion-ios-arrow-down"></span>
                                    </div>
                                    <ul class="links collapse" ${ isNormalMode ? 'id="injectables-links-module-CoreModule-f64cab7352eb4a3af4df788eb6ba6f67"' :
                                        'id="xs-injectables-links-module-CoreModule-f64cab7352eb4a3af4df788eb6ba6f67"' }>
                                        <li class="link">
                                            <a href="injectables/AuthErrorResponseHandler.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>AuthErrorResponseHandler</a>
                                        </li>
                                        <li class="link">
                                            <a href="injectables/DefaultErrorHandler.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>DefaultErrorHandler</a>
                                        </li>
                                        <li class="link">
                                            <a href="injectables/MethodNotAllowedErrorHandler.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>MethodNotAllowedErrorHandler</a>
                                        </li>
                                        <li class="link">
                                            <a href="injectables/NoConnectionErrorHandler.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>NoConnectionErrorHandler</a>
                                        </li>
                                        <li class="link">
                                            <a href="injectables/NotFoundErrorHandler.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>NotFoundErrorHandler</a>
                                        </li>
                                        <li class="link">
                                            <a href="injectables/ServerSideErrorHandler.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>ServerSideErrorHandler</a>
                                        </li>
                                        <li class="link">
                                            <a href="injectables/ValidationErrorHandler.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>ValidationErrorHandler</a>
                                        </li>
                                    </ul>
                                </li>
                            </li>
                            <li class="link">
                                <a href="modules/HeaderModule.html" data-type="entity-link">HeaderModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-HeaderModule-3a525d7b621dc615fa60fa6a5b9bbc00"' : 'data-target="#xs-components-links-module-HeaderModule-3a525d7b621dc615fa60fa6a5b9bbc00"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-HeaderModule-3a525d7b621dc615fa60fa6a5b9bbc00"' :
                                            'id="xs-components-links-module-HeaderModule-3a525d7b621dc615fa60fa6a5b9bbc00"' }>
                                            <li class="link">
                                                <a href="components/HeaderComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">HeaderComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/HeaderTitleComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">HeaderTitleComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                            </li>
                            <li class="link">
                                <a href="modules/HelpDialogModule.html" data-type="entity-link">HelpDialogModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-HelpDialogModule-0cd5ca3097a1fa10526de461cf310623"' : 'data-target="#xs-components-links-module-HelpDialogModule-0cd5ca3097a1fa10526de461cf310623"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-HelpDialogModule-0cd5ca3097a1fa10526de461cf310623"' :
                                            'id="xs-components-links-module-HelpDialogModule-0cd5ca3097a1fa10526de461cf310623"' }>
                                            <li class="link">
                                                <a href="components/HelpDialogComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">HelpDialogComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                            </li>
                            <li class="link">
                                <a href="modules/HomeModule.html" data-type="entity-link">HomeModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-HomeModule-247d9483a019b9c62ca28974c36b8c10"' : 'data-target="#xs-components-links-module-HomeModule-247d9483a019b9c62ca28974c36b8c10"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-HomeModule-247d9483a019b9c62ca28974c36b8c10"' :
                                            'id="xs-components-links-module-HomeModule-247d9483a019b9c62ca28974c36b8c10"' }>
                                            <li class="link">
                                                <a href="components/HomeComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">HomeComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                            </li>
                            <li class="link">
                                <a href="modules/ImprintModule.html" data-type="entity-link">ImprintModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-ImprintModule-9d11d861b3c651eff5db0b0203912b0f"' : 'data-target="#xs-components-links-module-ImprintModule-9d11d861b3c651eff5db0b0203912b0f"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-ImprintModule-9d11d861b3c651eff5db0b0203912b0f"' :
                                            'id="xs-components-links-module-ImprintModule-9d11d861b3c651eff5db0b0203912b0f"' }>
                                            <li class="link">
                                                <a href="components/ImprintComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">ImprintComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                            </li>
                            <li class="link">
                                <a href="modules/LoggingModule.html" data-type="entity-link">LoggingModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-LoggingModule-e46598e79a00c558fbb40158a6ce1cd7"' : 'data-target="#xs-components-links-module-LoggingModule-e46598e79a00c558fbb40158a6ce1cd7"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-LoggingModule-e46598e79a00c558fbb40158a6ce1cd7"' :
                                            'id="xs-components-links-module-LoggingModule-e46598e79a00c558fbb40158a6ce1cd7"' }>
                                            <li class="link">
                                                <a href="components/LoggingComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">LoggingComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                            </li>
                            <li class="link">
                                <a href="modules/LoginModule.html" data-type="entity-link">LoginModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-LoginModule-4e6d2eaf7fce1192a95262c2d37891f1"' : 'data-target="#xs-components-links-module-LoginModule-4e6d2eaf7fce1192a95262c2d37891f1"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-LoginModule-4e6d2eaf7fce1192a95262c2d37891f1"' :
                                            'id="xs-components-links-module-LoginModule-4e6d2eaf7fce1192a95262c2d37891f1"' }>
                                            <li class="link">
                                                <a href="components/LoginComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">LoginComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                            </li>
                            <li class="link">
                                <a href="modules/NavigationModule.html" data-type="entity-link">NavigationModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-NavigationModule-dd749978cc532c0294137f7c1664fe59"' : 'data-target="#xs-components-links-module-NavigationModule-dd749978cc532c0294137f7c1664fe59"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-NavigationModule-dd749978cc532c0294137f7c1664fe59"' :
                                            'id="xs-components-links-module-NavigationModule-dd749978cc532c0294137f7c1664fe59"' }>
                                            <li class="link">
                                                <a href="components/NavigationComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">NavigationComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                            </li>
                            <li class="link">
                                <a href="modules/PipeModule.html" data-type="entity-link">PipeModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#pipes-links-module-PipeModule-3c8ff26b3c8303b9f64b7b6074c671a6"' : 'data-target="#xs-pipes-links-module-PipeModule-3c8ff26b3c8303b9f64b7b6074c671a6"' }>
                                            <span class="icon ion-md-add"></span>
                                            <span>Pipes</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="pipes-links-module-PipeModule-3c8ff26b3c8303b9f64b7b6074c671a6"' :
                                            'id="xs-pipes-links-module-PipeModule-3c8ff26b3c8303b9f64b7b6074c671a6"' }>
                                            <li class="link">
                                                <a href="pipes/CustomDatePipe.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">CustomDatePipe</a>
                                            </li>
                                        </ul>
                                    </li>
                            </li>
                            <li class="link">
                                <a href="modules/PrivacyPolicyModule.html" data-type="entity-link">PrivacyPolicyModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-PrivacyPolicyModule-ea331cfdc0fa87aa700de6b709489eb5"' : 'data-target="#xs-components-links-module-PrivacyPolicyModule-ea331cfdc0fa87aa700de6b709489eb5"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-PrivacyPolicyModule-ea331cfdc0fa87aa700de6b709489eb5"' :
                                            'id="xs-components-links-module-PrivacyPolicyModule-ea331cfdc0fa87aa700de6b709489eb5"' }>
                                            <li class="link">
                                                <a href="components/PrivacyPolicyComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">PrivacyPolicyComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                            </li>
                            <li class="link">
                                <a href="modules/ToastMessageModule.html" data-type="entity-link">ToastMessageModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-ToastMessageModule-47678604744bd3ea9f839b3b9425ec31"' : 'data-target="#xs-components-links-module-ToastMessageModule-47678604744bd3ea9f839b3b9425ec31"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-ToastMessageModule-47678604744bd3ea9f839b3b9425ec31"' :
                                            'id="xs-components-links-module-ToastMessageModule-47678604744bd3ea9f839b3b9425ec31"' }>
                                            <li class="link">
                                                <a href="components/ToastMessageComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">ToastMessageComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                            </li>
                            <li class="link">
                                <a href="modules/UsersettingsModule.html" data-type="entity-link">UsersettingsModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-UsersettingsModule-e5a0e9beaaa8e10fa85b2fb86f6e34ed"' : 'data-target="#xs-components-links-module-UsersettingsModule-e5a0e9beaaa8e10fa85b2fb86f6e34ed"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-UsersettingsModule-e5a0e9beaaa8e10fa85b2fb86f6e34ed"' :
                                            'id="xs-components-links-module-UsersettingsModule-e5a0e9beaaa8e10fa85b2fb86f6e34ed"' }>
                                            <li class="link">
                                                <a href="components/UsersettingsComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">UsersettingsComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                            </li>
                </ul>
                </li>
                        <li class="chapter">
                            <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ? 'data-target="#injectables-links"' :
                                'data-target="#xs-injectables-links"' }>
                                <span class="icon ion-md-arrow-round-down"></span>
                                <span>Injectables</span>
                                <span class="icon ion-ios-arrow-down"></span>
                            </div>
                            <ul class="links collapse " ${ isNormalMode ? 'id="injectables-links"' : 'id="xs-injectables-links"' }>
                                <li class="link">
                                    <a href="injectables/ApiService.html" data-type="entity-link">ApiService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/AppService.html" data-type="entity-link">AppService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/AuthService.html" data-type="entity-link">AuthService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/DateService.html" data-type="entity-link">DateService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/GlobalErrorHandler.html" data-type="entity-link">GlobalErrorHandler</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/LocalStorageService.html" data-type="entity-link">LocalStorageService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/LoggingService.html" data-type="entity-link">LoggingService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/LoginService.html" data-type="entity-link">LoginService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/RouternavigationService.html" data-type="entity-link">RouternavigationService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/SessionStorageService.html" data-type="entity-link">SessionStorageService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/ToastMessageService.html" data-type="entity-link">ToastMessageService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/TranslateErrorHandler.html" data-type="entity-link">TranslateErrorHandler</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/UserService.html" data-type="entity-link">UserService</a>
                                </li>
                            </ul>
                        </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ? 'data-target="#interceptors-links"' :
                            'data-target="#xs-interceptors-links"' }>
                            <span class="icon ion-ios-swap"></span>
                            <span>Interceptors</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="interceptors-links"' : 'id="xs-interceptors-links"' }>
                            <li class="link">
                                <a href="interceptors/AddLanguageHeaderInterceptor.html" data-type="entity-link">AddLanguageHeaderInterceptor</a>
                            </li>
                            <li class="link">
                                <a href="interceptors/AuthInterceptor.html" data-type="entity-link">AuthInterceptor</a>
                            </li>
                        </ul>
                    </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ? 'data-target="#guards-links"' :
                            'data-target="#xs-guards-links"' }>
                            <span class="icon ion-ios-lock"></span>
                            <span>Guards</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="guards-links"' : 'id="xs-guards-links"' }>
                            <li class="link">
                                <a href="guards/AuthGuardService.html" data-type="entity-link">AuthGuardService</a>
                            </li>
                        </ul>
                    </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ? 'data-target="#interfaces-links"' :
                            'data-target="#xs-interfaces-links"' }>
                            <span class="icon ion-md-information-circle-outline"></span>
                            <span>Interfaces</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? ' id="interfaces-links"' : 'id="xs-interfaces-links"' }>
                            <li class="link">
                                <a href="interfaces/IExportLog.html" data-type="entity-link">IExportLog</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/IExportUser.html" data-type="entity-link">IExportUser</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/IHttpErrorResponseHandler.html" data-type="entity-link">IHttpErrorResponseHandler</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/IJWT.html" data-type="entity-link">IJWT</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ILanguage.html" data-type="entity-link">ILanguage</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ILog.html" data-type="entity-link">ILog</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ILogFilter.html" data-type="entity-link">ILogFilter</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/IPermission.html" data-type="entity-link">IPermission</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/IPrimeNgBase.html" data-type="entity-link">IPrimeNgBase</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/IUser.html" data-type="entity-link">IUser</a>
                            </li>
                        </ul>
                    </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ? 'data-target="#miscellaneous-links"'
                            : 'data-target="#xs-miscellaneous-links"' }>
                            <span class="icon ion-ios-cube"></span>
                            <span>Miscellaneous</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="miscellaneous-links"' : 'id="xs-miscellaneous-links"' }>
                            <li class="link">
                                <a href="miscellaneous/enumerations.html" data-type="entity-link">Enums</a>
                            </li>
                            <li class="link">
                                <a href="miscellaneous/functions.html" data-type="entity-link">Functions</a>
                            </li>
                            <li class="link">
                                <a href="miscellaneous/variables.html" data-type="entity-link">Variables</a>
                            </li>
                        </ul>
                    </li>
                        <li class="chapter">
                            <a data-type="chapter-link" href="routes.html"><span class="icon ion-ios-git-branch"></span>Routes</a>
                        </li>
                    <li class="chapter">
                        <a data-type="chapter-link" href="coverage.html"><span class="icon ion-ios-stats"></span>Documentation coverage</a>
                    </li>
            </ul>
        </nav>
        `);
        this.innerHTML = tp.strings;
    }
});