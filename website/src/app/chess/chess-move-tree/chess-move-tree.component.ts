import {Component, computed, input} from '@angular/core';
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
        const buildTree = (move: ChessOpeningMove): TreeNode => ({
            label: move.move,
            expanded: true,
            data: move,
            children: move.nextMoves.map(buildTree) // Recursively build child nodes
        });

        return [buildTree(data)]; // Start the recursion with the root data
    })
    showEvaluation = input<boolean>(false);

    protected readonly faCircleInfo = faCircleInfo;
    tooltipOptions = {
    };
    protected readonly Permissions = Permissions;
}
