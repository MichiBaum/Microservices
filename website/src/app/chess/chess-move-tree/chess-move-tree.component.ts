import {Component, computed, inject, input, output, signal} from '@angular/core';
import {OrganizationChart} from "primeng/organizationchart";
import {ChessOpening, ChessOpeningMove, WriteOpeningMove} from "../../core/models/chess/chess.models";
import {MenuItem, PrimeTemplate, TreeNode} from "primeng/api";
import {NgForOf, NgIf} from "@angular/common";
import {Tooltip} from "primeng/tooltip";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faCircleInfo} from "@fortawesome/free-solid-svg-icons";
import {
    ChessOpeningMoveFormDialogComponent
} from "../chess-opening-move-form-dialog/chess-opening-move-form-dialog.component";
import {ChessOpeningFormDialogComponent} from "../chess-opening-form-dialog/chess-opening-form-dialog.component";
import {Menubar} from "primeng/menubar";
import {PermissionService} from "../../core/services/permission.service";
import {Permissions} from "../../core/config/permissions";

@Component({
  selector: 'app-chess-move-tree',
    imports: [
        OrganizationChart,
        PrimeTemplate,
        NgIf,
        NgForOf,
        Tooltip,
        FaIconComponent,
        ChessOpeningMoveFormDialogComponent,
        ChessOpeningFormDialogComponent,
        Menubar,
    ],
  templateUrl: './chess-move-tree.component.html',
  styleUrl: './chess-move-tree.component.scss'
})
export class ChessMoveTreeComponent {
    private readonly permissionService = inject(PermissionService);
    protected readonly faCircleInfo = faCircleInfo;

    move = input<ChessOpeningMove>()
    moveTree = computed<TreeNode[]>(() => {
        const data = this.move();
        if (!data) return [];

        // Recursive function to transform ChessOpeningMove to TreeNode
        const buildTree = (move: ChessOpeningMove, layer: number): TreeNode => ({
            label: move.move,
            key: move.id,
            expanded: layer < 5,
            data: move,
            children: move.nextMoves.map((nextMove) => buildTree(nextMove, layer + 1))
        });

        return [buildTree(data, 0)]; // Start the recursion with the root data
    })
    showEvaluation = input<boolean>(false);
    reloadData = output<void>();

    moveDialogVisible = signal(false);
    moveInput = signal<WriteOpeningMove | undefined>(undefined);
    openNewMoveDialog(node: TreeNode) {
        const newMove: WriteOpeningMove = {
            id: '',
            move: '',
            parentMoveId: node.data.id,
        }
        this.moveInput.set(newMove)
        this.moveDialogVisible.set(true)
    }

    openingDialogVisible = signal(false);
    openingInput = signal<ChessOpening | undefined>(undefined);
    openNewOpeningDialog(node: TreeNode) {
        const newOpening: ChessOpening = {
            id: '',
            name: '',
            moveId: node.data.id,
        }
        this.openingInput.set(newOpening)
        this.openingDialogVisible.set(true)
    }

    reload() {
        this.reloadData.emit()
    }

    selectedMove = signal<TreeNode | undefined>(undefined);
    items = computed<MenuItem[]>(() => {
        const selectedMove = this.selectedMove()
        const hasAdminPermission = this.permissionService.hasAnyOf([Permissions.CHESS_SERVICE_ADMIN])
        return [
            {
                label: 'Inspect Opening',
                visible: selectedMove != undefined,
            },
            {
                label: 'Select as Root',
                visible: selectedMove != undefined,
                routerLink: '/chess/openings/' + selectedMove?.data.openingId,
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

}
