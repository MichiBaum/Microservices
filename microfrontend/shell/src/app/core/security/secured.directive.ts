import {Directive, Input, TemplateRef, ViewContainerRef} from '@angular/core';
import {PermissionEnum} from '../models/enum/permission.enum';
import {AuthService} from '../services/auth.service';

@Directive({
  selector: '[anyPermission]'
})
export class SecuredDirective {

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private authService: AuthService
  ) { }

  @Input() set anyPermission(permissions: PermissionEnum[]) {
    if (this.authService.hasAnyPermission(permissions)) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }
}
