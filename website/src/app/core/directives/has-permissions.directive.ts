import {
    Directive,
    inject, input, OnInit,
    TemplateRef,
    ViewContainerRef
} from '@angular/core';
import {PermissionService} from "../services/permission.service";
import {Permissions} from "../config/permissions";

@Directive({
  selector: '[hasPermissions]'
})
export class HasPermissionsDirective implements OnInit{
    private readonly permissionService = inject(PermissionService)

    hasPermissions = input<Permissions[]>([])

    constructor(
        private templateRef: TemplateRef<any>,
        private viewContainer: ViewContainerRef
    ) {
    }

    ngOnInit(): void {
        this.updateView(this.hasPermissions())
    }

    private updateView(permissions: Permissions[]) {
        const hasPermissions = this.permissionService.hasAnyOf(permissions)
        if (hasPermissions) {
            this.viewContainer.createEmbeddedView(this.templateRef);
        } else {
            this.viewContainer.clear();
        }
    }

}
