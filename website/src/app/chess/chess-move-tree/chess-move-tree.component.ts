import {Component, computed, input, output, signal} from '@angular/core';
import {OrganizationChart} from "primeng/organizationchart";
import {ChessOpening, ChessOpeningMove, WriteOpeningMove} from "../../core/models/chess/chess.models";
import {MenuItem, PrimeTemplate, TreeNode} from "primeng/api";
import {NgForOf, NgIf, NgStyle} from "@angular/common";
import {Tooltip} from "primeng/tooltip";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faCircleInfo} from "@fortawesome/free-solid-svg-icons";
import {Button} from "primeng/button";
import {HasPermissionsDirective} from "../../core/directives/has-permissions.directive";
import {Permissions} from "../../core/config/permissions";
import {
    ChessOpeningMoveFormDialogComponent
} from "../chess-opening-move-form-dialog/chess-opening-move-form-dialog.component";
import {ChessOpeningFormDialogComponent} from "../chess-opening-form-dialog/chess-opening-form-dialog.component";
import {ButtonGroup} from "primeng/buttongroup";
import {Dock} from "primeng/dock";
import {Menubar} from "primeng/menubar";
import {Toolbar} from "primeng/toolbar";

@Component({
  selector: 'app-chess-move-tree',
    imports: [
        OrganizationChart,
        PrimeTemplate,
        NgIf,
        NgForOf,
        Tooltip,
        FaIconComponent,
        Button,
        HasPermissionsDirective,
        ChessOpeningMoveFormDialogComponent,
        ChessOpeningFormDialogComponent,
        ButtonGroup,
        Dock,
        NgStyle,
        Menubar,
        Toolbar,
    ],
  templateUrl: './chess-move-tree.component.html',
  styleUrl: './chess-move-tree.component.scss'
})
export class ChessMoveTreeComponent {
    protected readonly faCircleInfo = faCircleInfo;
    protected readonly Permissions = Permissions;

    move = input<ChessOpeningMove>()
    moveTree = computed<TreeNode[]>(() => {
        const data = this.move();
        if (!data) return [];

        // Recursive function to transform ChessOpeningMove to TreeNode
        const buildTree = (move: ChessOpeningMove, layer: number): TreeNode => ({
            label: move.move,
            key: move.id,
            expanded: layer < 6,
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
    items: MenuItem[] = [
        {
            label: 'Add Opening', command: () => {
                const selectedMove = this.selectedMove()
                if(selectedMove == undefined) return
                this.openNewOpeningDialog(selectedMove)
            }
        },
        {
            label: 'Add Move', command: () => {
                const selectedMove = this.selectedMove()
                if(selectedMove == undefined) return
                this.openNewMoveDialog(selectedMove)
            }
        }
    ];
}
