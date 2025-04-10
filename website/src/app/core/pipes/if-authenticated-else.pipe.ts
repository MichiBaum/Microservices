import {inject, Pipe, PipeTransform} from '@angular/core';
import {PermissionService} from "../services/permission.service";

@Pipe({
  name: 'ifAuthenticatedElse'
})
export class IfAuthenticatedElsePipe implements PipeTransform {

    permissionService = inject(PermissionService);

    transform(authenticatedContent: string, unauthenticatedContent: string): string {
        return this.permissionService.isAuthenticated()
            ? authenticatedContent
            : unauthenticatedContent;
    }


}
