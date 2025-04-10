import {inject, Pipe, PipeTransform} from '@angular/core';
import {PermissionService} from "../services/permission.service";
import {Side, Sides} from "../config/sides";

@Pipe({
  name: 'canActivateElse'
})
export class CanActivateElsePipe implements PipeTransform {

    permissionService = inject(PermissionService);

    transform(authenticatedContent: string, unauthenticatedContent: string, side: Side): string {
        return side.canActivate(this.permissionService)
            ? authenticatedContent
            : unauthenticatedContent;
    }


}
