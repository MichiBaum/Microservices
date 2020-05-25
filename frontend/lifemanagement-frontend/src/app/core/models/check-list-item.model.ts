export interface CheckListItem {
  id: number;
  name: string;
  description: string;
  creationDate: number;
  checkListItems: CheckListItem[];
}
