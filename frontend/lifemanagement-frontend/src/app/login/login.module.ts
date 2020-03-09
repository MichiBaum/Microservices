import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {TranslateModule} from '@ngx-translate/core';
import {ButtonModule, InputTextModule} from 'primeng';
import {LoginComponent} from './login.component';

@NgModule({
    declarations: [
        LoginComponent
    ],
    exports: [
        LoginComponent
    ],
    imports: [
        CommonModule,
        InputTextModule,
        TranslateModule,
        ReactiveFormsModule,
        ButtonModule
    ]
})
export class LoginModule { }
