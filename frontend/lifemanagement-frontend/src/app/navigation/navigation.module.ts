import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
import {ButtonModule, MenuModule, SidebarModule} from 'primeng';
import { NavigationComponent } from './navigation.component';



@NgModule({
    declarations: [
      NavigationComponent
    ],
    exports: [
        NavigationComponent
    ],
  imports: [
    CommonModule,
    SidebarModule,
    TranslateModule,
    ButtonModule,
    MenuModule
  ]
})
export class NavigationModule { }
