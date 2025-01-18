import {Component, computed, input, signal} from '@angular/core';
import {OrganizationChart} from "primeng/organizationchart";
import {ChessOpeningMove} from "../../core/models/chess/chess.models";
import {PrimeTemplate, TreeNode} from "primeng/api";
import {NgForOf, NgIf} from "@angular/common";
import {Tooltip} from "primeng/tooltip";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faCircleInfo} from "@fortawesome/free-solid-svg-icons";
import {Button} from "primeng/button";
import {HasPermissionsDirective} from "../../core/directives/has-permissions.directive";
import {Permissions} from "../../core/config/permissions";
import {Dialog} from "primeng/dialog";
import {ChessOpeningMoveFormComponent} from "../chess-opening-move-form/chess-opening-move-form.component";
import {
    ChessOpeningMoveFormDialogComponent
} from "../chess-opening-move-form-dialog/chess-opening-move-form-dialog.component";
import {ChessOpeningFormDialogComponent} from "../chess-opening-form-dialog/chess-opening-form-dialog.component";

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
        Dialog,
        ChessOpeningMoveFormComponent,
        ChessOpeningMoveFormDialogComponent,
        ChessOpeningFormDialogComponent,
    ],
  templateUrl: './chess-move-tree.component.html',
  styleUrl: './chess-move-tree.component.scss'
})
export class ChessMoveTreeComponent {

    move = input<ChessOpeningMove>()
    moveTree = computed<TreeNode[]>(() => {
        const data = this.move();
        if (!data) return [];

        // Recursive function to transform ChessOpeningMove to TreeNode
        const buildTree = (move: ChessOpeningMove, layer: number): TreeNode => ({
            label: move.move,
            expanded: layer < 5,
            data: move,
            children: move.nextMoves.map(buildTree) // Recursively build child nodes
        });

        return [buildTree(data, 0)]; // Start the recursion with the root data
    })
    showEvaluation = input<boolean>(false);

    protected readonly faCircleInfo = faCircleInfo;
    tooltipOptions = {
    };
    protected readonly Permissions = Permissions;

    moveDialogVisible = signal(false);
    showMoveDialog(node: any) {
        this.moveDialogVisible.set(true)
    }
    openingDialogVisible = signal(false);
    showOpeningDialog(node: any) {
        this.openingDialogVisible.set(true)
    }
}
