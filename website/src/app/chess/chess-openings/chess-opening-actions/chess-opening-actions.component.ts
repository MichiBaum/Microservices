import {Component, computed, inject, input, output, signal} from '@angular/core';
import {MenuItem} from "primeng/api";
import {Permissions} from "../../../core/config/permissions";
import {PermissionService} from "../../../core/services/permission.service";
import {Menubar} from "primeng/menubar";
import {ChessOpening, WriteOpeningMove} from "../../../core/models/chess/chess.models";
import {ChessOpeningFormDialogComponent} from "../../chess-opening-form-dialog/chess-opening-form-dialog.component";
import {
    ChessOpeningMoveFormDialogComponent
} from "../../chess-opening-move-form-dialog/chess-opening-move-form-dialog.component";
import {
    ChessOpeningMoveEvaluationFormDialogComponent
} from "../../chess-opening-move-evaluation-form-dialog/chess-opening-move-evaluation-form-dialog.component";
import {SelectedMove} from "../../chess-move-tree/selected-move.model";

@Component({
  selector: 'app-chess-opening-actions',
    imports: [
        Menubar,
        ChessOpeningFormDialogComponent,
        ChessOpeningMoveFormDialogComponent,
        ChessOpeningMoveEvaluationFormDialogComponent
    ],
  templateUrl: './chess-opening-actions.component.html',
  styleUrl: './chess-opening-actions.component.scss'
})
export class ChessOpeningActionsComponent {
    private readonly permissionService = inject(PermissionService);

    selectedMove = input<SelectedMove | undefined>(undefined);
    items = computed<MenuItem[]>(() => {
        const selectedMove = this.selectedMove()
        const hasAdminPermission = this.permissionService.hasAnyOf([Permissions.CHESS_SERVICE_ADMIN])
        const hasOpening = selectedMove?.openingId != undefined
        return [
            {
                label: 'Inspect Opening',
                visible: selectedMove != undefined && hasOpening,
            },
            {
                label: 'Select as Root',
                visible: selectedMove != undefined && hasOpening,
                routerLink: '/chess/openings/' + selectedMove?.openingId,
            },
            {
                label: 'Add/Edit Opening',
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
            },
            {
                label: 'Edit Move',
                visible: selectedMove != undefined && hasAdminPermission,
                command: () => {
                    const selectedMove = this.selectedMove()
                    if(selectedMove == undefined) return
                    this.openEditMoveDialog(selectedMove)
                }
            },
            {
                label: 'Evaluations',
                visible: selectedMove != undefined && hasAdminPermission,
                command: () => {
                    const selectedMove = this.selectedMove()
                    if(selectedMove == undefined) return
                    this.openEvaluationDialog(selectedMove)
                }
            }
        ];
    });

    moveDialogVisible = signal(false);
    moveInput = signal<WriteOpeningMove | undefined>(undefined);
    openNewMoveDialog(move: SelectedMove) {
        const newMove: WriteOpeningMove = {
            id: '',
            move: '',
            parentMoveId: move.id,
        }
        this.moveInput.set(newMove)
        this.moveDialogVisible.set(true)
    }
    private openEditMoveDialog(move: SelectedMove) {
        const newMove: WriteOpeningMove = {
            id: move.id,
            move: move.move,
            parentMoveId: move.parentId,
        }
        this.moveInput.set(newMove)
        this.moveDialogVisible.set(true)
    }

    openingDialogVisible = signal(false);
    openingInput = signal<ChessOpening | undefined>(undefined);
    openNewOpeningDialog(move: SelectedMove) {
        const newOpening: ChessOpening = {
            id: move.openingId ?? '',
            name: move.openingName ?? '',
            moveId: move.id,
        }
        this.openingInput.set(newOpening)
        this.openingDialogVisible.set(true)
    }

    evaluationDialogVisible = signal(false);
    evaluationInput = signal<SelectedMove | undefined>(undefined);
    openEvaluationDialog(move: SelectedMove) {
        this.evaluationInput.set(move)
        this.evaluationDialogVisible.set(true)
    }

    reloadData = output<void>();
    reload() {
        this.reloadData.emit()
    }

}
