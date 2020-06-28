import {EmitterType} from '../shared/models/emitter-type.model';

export class MockSseService {
  public closeEvent(type: EmitterType): void {}
  public openEvent(type: EmitterType, callback: () => void = null): void {}
}
