import {Component, computed, Input, input, output} from '@angular/core';
import {
    OrganizationChart,
    OrganizationChartNodeSelectEvent,
    OrganizationChartNodeUnSelectEvent
} from "primeng/organizationchart";
import {ChessOpeningMove} from "../../core/models/chess/chess.models";
import {PrimeTemplate, TreeNode} from "primeng/api";

import {Tooltip} from "primeng/tooltip";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faCircleInfo} from "@fortawesome/free-solid-svg-icons";
import {SelectedMove} from "./selected-move.model";
import {TranslatePipe} from "@ngx-translate/core";

@Component({
  selector: 'app-chess-move-tree',
    imports: [
    OrganizationChart,
    PrimeTemplate,
    Tooltip,
    FaIconComponent,
    TranslatePipe
],
  templateUrl: './chess-move-tree.component.html',
  styleUrl: './chess-move-tree.component.css'
})
export class ChessMoveTreeComponent {
    protected readonly faCircleInfo = faCircleInfo;

    showEvaluation = input<boolean>(false);
    treeDepth = input.required<number>();
    rootMove = input<ChessOpeningMove>()
    moveTree = computed<TreeNode[]>(() => {
        const data = this.rootMove();
        if (!data) return [];

        const buildTree = (move: ChessOpeningMove, layer: number): TreeNode => ({
            label: move.move,
            key: move.id,
            expanded: layer < this.treeDepth(),
            data: move,
            children: move.nextMoves.map((nextMove) => buildTree(nextMove, layer + 1))
        });

        return [buildTree(data, 0)];
    })

    selectedMove = output<SelectedMove | undefined>();
    onNodeSelect(event: OrganizationChartNodeSelectEvent) {
        const move = event.node.data as ChessOpeningMove;
        const parent = event.node.parent?.data as ChessOpeningMove | undefined;

        const selectedMove: SelectedMove = {
            id: move.id,
            move: move.move,
            fen: move.fen,
            parentId: parent?.id ?? '',
            openingId: move.openingId ?? '',
            openingName: move.openingName ?? '',
        }

        this.selectedMove.emit(selectedMove)
    }
    onNodeUnselect(event: OrganizationChartNodeUnSelectEvent) {
        this.selectedMove.emit(undefined)
    }
}


