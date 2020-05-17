import {Component, TemplateRef, Input, HostBinding} from '@angular/core';
import { ToastService } from '../toast.service';

@Component({
  selector: 'pkr-toast-container',
  templateUrl: './toast-container.component.html',
  styleUrls: ['./toast-container.component.scss']
})
export class ToastContainerComponent {
  @HostBinding('class.ngb-toasts') true;
  @Input() header: string;

  constructor(public toastService: ToastService) { }

  public isTemplate(toast: any) {
    return toast.textOrTpl instanceof TemplateRef;
  }
}
