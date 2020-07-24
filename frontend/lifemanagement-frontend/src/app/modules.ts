import {CoreModule} from './core/core.module';
import {PipeModule} from './core/pipes/pipe.module';
import {HeaderModule} from './header/header.module';
import {HelpDialogModule} from './help-dialog/help-dialog.module';
import {HomeModule} from './home/home.module';
import {ImprintModule} from './imprint/imprint.module';
import {LoginModule} from './login/login.module';
import {LoggingModule} from './logs/logging.module';
import {NavigationModule} from './navigation/navigation.module';
import {PrivacyPolicyModule} from './privacy-policy/privacy-policy.module';
import {ToastMessageModule} from './toast-message/toast-message.module';
import {UsersettingsModule} from './usersettings/usersettings.module';

/**
 * All own modules
 */
export const MyModules = [
  CoreModule,
  LoginModule,
  HomeModule,
  HeaderModule,
  NavigationModule,
  LoggingModule,
  HelpDialogModule,
  PipeModule,
  UsersettingsModule,
  ToastMessageModule,
  ImprintModule,
  PrivacyPolicyModule
];
