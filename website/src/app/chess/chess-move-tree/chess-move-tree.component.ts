import {Component, computed, input, output, signal} from '@angular/core';
import {OrganizationChart} from "primeng/organizationchart";
import {ChessOpeningMove} from "../../core/models/chess/chess.models";
import {PrimeTemplate, TreeNode} from "primeng/api";
import {NgForOf, NgIf} from "@angular/common";
import {Tooltip} from "primeng/tooltip";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faCircleInfo} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-chess-move-tree',
    imports: [
        OrganizationChart,
        PrimeTemplate,
        NgIf,
        NgForOf,
        Tooltip,
        FaIconComponent,
    ],
  templateUrl: './chess-move-tree.component.html',
  styleUrl: './chess-move-tree.component.scss'
})
export class ChessMoveTreeComponent {
    protected readonly faCircleInfo = faCircleInfo;

    showEvaluation = input<boolean>(false);
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

    selectedMove = output<ChessOpeningMove | undefined>();
    onSelectionChange(node: TreeNode) {
        this.selectedMove.emit(node.data);
    }
}
