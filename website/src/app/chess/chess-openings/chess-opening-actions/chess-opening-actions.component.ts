import {Component, computed, inject, input, output, signal} from '@angular/core';
import {MenuItem} from "primeng/api";
import {Permissions} from "../../../core/config/permissions";
import {PermissionService} from "../../../core/services/permission.service";
import {Menubar} from "primeng/menubar";
import {ChessOpening, ChessOpeningMove, WriteOpeningMove} from "../../../core/models/chess/chess.models";
import {ChessOpeningFormDialogComponent} from "../../chess-opening-form-dialog/chess-opening-form-dialog.component";
import {
    ChessOpeningMoveFormDialogComponent
} from "../../chess-opening-move-form-dialog/chess-opening-move-form-dialog.component";

@Component({
  selector: 'app-chess-opening-actions',
    imports: [
        Menubar,
        ChessOpeningFormDialogComponent,
        ChessOpeningMoveFormDialogComponent
    ],
  templateUrl: './chess-opening-actions.component.html',
  styleUrl: './chess-opening-actions.component.scss'
})
export class ChessOpeningActionsComponent {
    private readonly permissionService = inject(PermissionService);

    selectedMove = input<ChessOpeningMove | undefined>(undefined);
    items = computed<MenuItem[]>(() => {
        const selectedMove = this.selectedMove()
        const hasAdminPermission = this.permissionService.hasAnyOf([Permissions.CHESS_SERVICE_ADMIN])
        return [
            {
                label: 'To Start',
                routerLink: '/chess/openings/'
            },
            {
                label: 'Inspect Opening',
                visible: selectedMove != undefined,
            },
            {
                label: 'Select as Root',
                visible: selectedMove != undefined,
                routerLink: '/chess/openings/' + selectedMove?.openingId,
            },
            {
                label: 'Add Opening',
                visible: selectedMove != undefined && hasAdminPermission,
                command: () => {
                    const selectedMove = this.selectedMove()
                    if(selectedMove == undefined) return
                    this.openNewOpeningDialog(selectedMove)
                }
            },
            {
                label: 'Add Move',
                visible: selectedMove != undefined && hasAdminPermission,
                command: () => {
                    const selectedMove = this.selectedMove()
                    if(selectedMove == undefined) return
                    this.openNewMoveDialog(selectedMove)
                }
            }
        ];
    });

    moveDialogVisible = signal(false);
    moveInput = signal<WriteOpeningMove | undefined>(undefined);
    openNewMoveDialog(move: ChessOpeningMove) {
        const newMove: WriteOpeningMove = {
            id: '',
            move: '',
            parentMoveId: move.id,
        }
        this.moveInput.set(newMove)
        this.moveDialogVisible.set(true)
    }

    openingDialogVisible = signal(false);
    openingInput = signal<ChessOpening | undefined>(undefined);
    openNewOpeningDialog(move: ChessOpeningMove) {
        const newOpening: ChessOpening = {
            id: '',
            name: '',
            moveId: move.id,
        }
        this.openingInput.set(newOpening)
        this.openingDialogVisible.set(true)
    }

    reloadData = output<void>();
    reload() {
        this.reloadData.emit()
    }

}
