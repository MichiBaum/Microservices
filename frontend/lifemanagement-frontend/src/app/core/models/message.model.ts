import {IMessageType} from './message-type.model';

export interface IMessage {
  type: IMessageType;
  message: string;
  blocking?: boolean;
}
